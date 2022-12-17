package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.misc

import net.minecraft.client.gui.screen.DownloadingTerrainScreen
import net.minecraft.entity.TrackedPosition
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.ClientConnection
import net.minecraft.network.NetworkState
import net.minecraft.network.Packet
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket
import net.minecraft.network.packet.c2s.play.PlayPongC2SPacket
import net.minecraft.network.packet.s2c.play.*
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShapes
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.*
import net.tarasandedevelopment.tarasande.injection.accessor.IClientConnection
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBind
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueColor
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.Graph
import net.tarasandedevelopment.tarasande.util.extension.minecraft.packet.isNewWorld
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import org.lwjgl.glfw.GLFW
import su.mandora.event.EventDispatcher
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class ModuleBlink : Module("Blink", "Delays packets", ModuleCategory.MISC) {

    private val affectedPackets = ValueMode(this, "Affected packets", true, "Serverbound", "Clientbound")
    private val mode = object : ValueMode(this, "Mode", false, "State-dependent", "Pulse blink", "Latency") {
        override fun onChange() = onDisable()
    }
    private val pulseDelay = object : ValueNumber(this, "Pulse delay", 0.0, 500.0, 1000.0, 1.0) {
        override fun isEnabled() = mode.isSelected(1)
        override fun onChange() = onDisable()
    }
    private val latency = object : ValueNumber(this, "Latency", 0.0, 500.0, 1000.0, 1.0) {
        override fun isEnabled() = mode.isSelected(2)
        override fun onChange() = onDisable()
    }
    private val cancelKey = object : ValueBind(this, "Cancel key", Type.KEY, GLFW.GLFW_KEY_UNKNOWN) {
        override fun isEnabled() = mode.isSelected(0) && affectedPackets.isSelected(0)
    }
    private val restrictPackets = object : ValueMode(this, "Restrict packets", true, "Keep alive", "Ping") {
        override fun isEnabled() = affectedPackets.anySelected()
    }
    private val hitBoxColor = object : ValueColor(this, "Hit box color", 0.0, 1.0, 1.0, 1.0) {
        override fun isEnabled() = affectedPackets.isSelected(1)
    }

    private val packets = CopyOnWriteArrayList<Triple<Packet<*>, EventPacket.Type, Long>>()
    private val timeUtil = TimeUtil()

    private var pos: Vec3d? = null
    private var velocity: Vec3d? = null
    private var rotation: Rotation? = null

    private val newPositions = ConcurrentHashMap<Int, TrackedPosition>()

    init {
        // Enable both by default
        autoDisable.select(0)
        autoDisable.select(1)

        TarasandeMain.managerGraph().add(object : Graph("Blink", "Last transaction", 50, true) {
            init {
                EventDispatcher.add(EventPacket::class.java) { event ->
                    if (event.type == EventPacket.Type.RECEIVE && event.packet is PlayPingS2CPacket)
                        add(event.packet.parameter)
                }
            }
        })

        EventDispatcher.apply {
            add(EventPacket::class.java, 1) { event ->
                if (event.type == EventPacket.Type.RECEIVE) {
                    when (event.packet) {
                        is PlayerSpawnS2CPacket -> {
                            newPositions[event.packet.id] = TrackedPosition()
                        }

                        is EntityS2CPacket -> {
                            event.packet.apply {
                                if (positionChanged) {
                                    newPositions[id]?.also { it.setPos(it.withDelta(deltaX.toLong(), deltaY.toLong(), deltaZ.toLong())) }
                                }
                            }
                        }

                        is EntityPositionS2CPacket -> {
                            newPositions[event.packet.id]?.setPos(event.packet.let { Vec3d(it.x, it.y, it.z) })
                        }

                        is EntitiesDestroyS2CPacket -> {
                            event.packet.entityIds.forEach {
                                newPositions.remove(it)
                            }
                        }

                        is PlayerRespawnS2CPacket -> {
                            if(event.packet.isNewWorld())
                                newPositions.clear()
                        }
                    }
                }
            }
            add(EventDisconnect::class.java) {
                if (it.connection == mc.networkHandler?.connection)
                    newPositions.clear()
            }
        }
    }

    override fun onEnable() {
        pos = mc.player?.pos
        velocity = mc.player?.velocity
        rotation = Rotation(mc.player ?: return)
        packets.clear() // This might be filled with data, because it is copy on write
    }

    init {
        registerEvent(EventPacket::class.java, 9999) { event ->
            if (event.cancelled) return@registerEvent
            if (event.packet != null) {
                if (mc.networkHandler?.connection?.channel?.attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY)?.get() != NetworkState.PLAY) {
                    packets.clear() // Packets don't matter anymore
                    return@registerEvent
                }
                if (mode.isSelected(1) && mc.currentScreen is DownloadingTerrainScreen) {
                    onDisable()
                    return@registerEvent
                }
                if (
                    (event.type == EventPacket.Type.SEND && event.packet is ClientStatusC2SPacket && event.packet.mode == ClientStatusC2SPacket.Mode.PERFORM_RESPAWN) ||
                    (event.type == EventPacket.Type.RECEIVE && event.packet is PlayerRespawnS2CPacket)
                ) {
                    onDisable()
                    return@registerEvent
                }
                if (affectedPackets.isSelected(event.type.ordinal)) {
                    if (restrictPackets.anySelected() &&
                        restrictPackets.isSelected(0) && event.packet !is KeepAliveC2SPacket && event.packet !is KeepAliveS2CPacket &&
                        restrictPackets.isSelected(1) && event.packet !is PlayPongC2SPacket && event.packet !is PlayPingS2CPacket) {
                        return@registerEvent
                    }

                    packets.add(Triple(event.packet, event.type,
                        if (mode.isSelected(2))
                            System.currentTimeMillis() + latency.value.toLong()
                        else
                            System.currentTimeMillis()
                    ))
                    event.cancelled = true
                }
            }
        }

        registerEvent(EventPollEvents::class.java, 9999) {
            when {
                mode.isSelected(1) -> {
                    if (timeUtil.hasReached(pulseDelay.value.toLong())) {
                        onDisable(true)
                        timeUtil.reset()
                    }
                }

                mode.isSelected(2) -> onDisable(false)
            }
        }

        registerEvent(EventTick::class.java, 9999) { event ->
            if (event.state == EventTick.State.PRE) {
                if (pos == null || velocity == null || rotation == null)
                    onEnable()

                if (cancelKey.isEnabled())
                    if (cancelKey.wasPressed() > 0) {
                        packets.removeIf { it.second == EventPacket.Type.SEND }
                        onDisable(all = true, cancelled = true)
                        enabled = false
                    }
            }
        }

        registerEvent(EventRender3D::class.java) { event ->
            if (affectedPackets.isSelected(1) && !restrictPackets.anySelected())
                newPositions.forEach { (_, trackedPosition) -> RenderUtil.blockOutline(event.matrices, VoxelShapes.cuboid(PlayerEntity.STANDING_DIMENSIONS.getBoxAt(trackedPosition.subtract(Vec3d.ZERO).negate())), hitBoxColor.getColor().rgb) }
        }
    }

    override fun onDisable() {
        onDisable(true)
    }

    fun onDisable(all: Boolean, cancelled: Boolean = false, timeOffset: Long = 0L) {
        if (mc.networkHandler?.connection?.isOpen == true) {
            val copy = ArrayList<Triple<Packet<*>, EventPacket.Type, Long>>()
            packets.removeIf {
                if (all || System.currentTimeMillis() + timeOffset >= it.third) {
                    copy.add(it)
                    true
                } else
                    false
            }
            for (triple in copy) {
                if (all || System.currentTimeMillis() + timeOffset >= triple.third) {
                    when (triple.second) {
                        EventPacket.Type.SEND -> (mc.networkHandler?.connection as IClientConnection).tarasande_forceSend(triple.first)
                        EventPacket.Type.RECEIVE ->
                            if (mc.networkHandler?.connection?.packetListener is ClientPlayPacketListener) {
                                try {
                                    @Suppress("UNCHECKED_CAST") // generics are so cool
                                    (triple.first as Packet<ClientPlayPacketListener>).apply(mc.networkHandler?.connection?.packetListener as ClientPlayPacketListener)
                                } catch (_: Exception) {
                                }
                            }
                    }
                }
            }
        } else
            packets.clear()
        if (cancelled) {
            mc.player?.setPosition(pos)
            mc.player?.velocity = velocity
            mc.player?.yaw = rotation?.yaw!!
            mc.player?.pitch = rotation?.pitch!!
        }
    }
}
