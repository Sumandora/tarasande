package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.misc

import net.minecraft.client.gui.screen.DownloadingTerrainScreen
import net.minecraft.entity.Entity
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
import net.tarasandedevelopment.tarasande.event.*
import net.tarasandedevelopment.tarasande.injection.accessor.IClientConnection
import net.tarasandedevelopment.tarasande.injection.accessor.ILivingEntity
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.*
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.Graph
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.ManagerGraph
import net.tarasandedevelopment.tarasande.util.math.MathUtil
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import org.lwjgl.glfw.GLFW
import su.mandora.event.EventDispatcher
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class ModuleBlink : Module("Blink", "Delays packets", ModuleCategory.MISC) {

    private val affectedPackets = ValueMode(this, "Affected packets", true, "Serverbound", "Clientbound")
    private val mode = object : ValueMode(this, "Mode", false, "State-dependent", "Pulse blink", "Latency", "Automatic") {
        override fun onChange(index: Int, oldSelected: Boolean, newSelected: Boolean) = onDisable()
    }
    private val pulseDelay = object : ValueNumber(this, "Pulse delay", 0.0, 500.0, 1000.0, 10.0) {
        override fun isEnabled() = mode.isSelected(1) || mode.isSelected(3)
        override fun onChange(oldValue: Double?, newValue: Double) {
            @Suppress("SENSELESS_COMPARISON")
            if(packets != null)
                onDisable()
        }
    }
    private val latency = object : ValueNumber(this, "Latency", 0.0, 500.0, 1000.0, 10.0) {
        override fun isEnabled() = mode.isSelected(2)
        override fun onChange(oldValue: Double?, newValue: Double) {
            @Suppress("SENSELESS_COMPARISON")
            if(packets != null)
                onDisable()
        }
    }
    private val reach = object : ValueNumber(this, "Reach", 0.1, 6.0, 15.0, 0.1) {
        override fun isEnabled() = mode.isSelected(3)
        override fun onChange(oldValue: Double?, newValue: Double) {
            @Suppress("SENSELESS_COMPARISON")
            if(packets != null)
                onDisable()
        }
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
    private val ignoreChunks = ValueBoolean(this, "Ignore chunks", true)

    private var packets = CopyOnWriteArrayList<Triple<Packet<*>, EventPacket.Type, Long>>()
    private val timeUtil = TimeUtil()

    private var pos: Vec3d? = null
    private var velocity: Vec3d? = null
    private var rotation: Rotation? = null

    private val newPositions = ConcurrentHashMap<Entity, Vec3d>()

    init {
        // Enable both by default
        autoDisable.select(0)
        autoDisable.select(1)

        ManagerGraph.add(object : Graph("Blink", "Last transaction", 50, true) {
            init {
                EventDispatcher.add(EventPacket::class.java) { event ->
                    if (event.type == EventPacket.Type.RECEIVE && event.packet is PlayPingS2CPacket)
                        add(event.packet.parameter)
                }
            }
        })
    }

    override fun onEnable() {
        pos = mc.player?.pos
        velocity = mc.player?.velocity
        rotation = Rotation(mc.player ?: return)
        packets.clear() // This might be filled with data, because it is copy on write
        newPositions.clear()
    }

    private fun shouldPulsate(): Boolean {
        // POV: Du hast Esounds Leben in wenigen Minuten useless gemacht $$$

        //@formatter:off
        val validEntities =
            mc.world?.entities?.
            asSequence()?.
            filterIsInstance<PlayerEntity>()?.
            filter { PlayerUtil.isAttackable(it) }?.
            filter { mc.player?.eyePos?.squaredDistanceTo(MathUtil.closestPointToBox(mc.player?.eyePos!!, it.boundingBox.expand(it.targetingMargin.toDouble())))!! <= reach.value * reach.value }?.
            toList()!!
        //@formatter:on
        if (validEntities.isEmpty())
            return false

        return validEntities.all {
            val newPosition = newPositions[it] ?: return false
            val lastPosition = (it as ILivingEntity).tarasande_prevServerPos() ?: return false

            val dimensions = it.getDimensions(it.pose)

            val actualDist = mc.player?.eyePos?.distanceTo(MathUtil.closestPointToBox(mc.player?.eyePos!!, dimensions.getBoxAt(newPosition).expand(it.targetingMargin.toDouble())))!!
            // To work around the issue that anticipateBoundingBox poses, we force the same bounding box here (this is probably the most rofl "solution", there is and there are still ways it could fail)
            val oldDist = mc.player?.eyePos?.distanceTo(MathUtil.closestPointToBox(mc.player?.eyePos!!, dimensions.getBoxAt(lastPosition).expand(it.targetingMargin.toDouble())))!!

            return oldDist < actualDist
        }
    }

    init {
        registerEvent(EventPacket::class.java, 9999) { event ->
            if (event.cancelled) return@registerEvent
            if (event.packet != null) {
                if (mc.networkHandler?.connection?.channel?.attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY)?.get() != NetworkState.PLAY) {
                    packets.clear() // Packets don't matter anymore
                    return@registerEvent
                }
                if (
                    mc.currentScreen is DownloadingTerrainScreen ||
                    (event.type == EventPacket.Type.SEND && event.packet is ClientStatusC2SPacket && event.packet.mode == ClientStatusC2SPacket.Mode.PERFORM_RESPAWN) ||
                    (event.type == EventPacket.Type.RECEIVE && (event.packet is PlayerRespawnS2CPacket || (ignoreChunks.value && (event.packet is ChunkDataS2CPacket || event.packet is LightUpdateS2CPacket))))
                ) {
                    onDisable(true)
                    onEnable()
                    return@registerEvent
                }

                // Entity position tracker
                when (event.packet) {
                    is EntityS2CPacket -> {
                        event.packet.apply {
                            if (positionChanged) {
                                val entity = mc.world?.getEntityById(event.packet.id)
                                if (entity != null) {
                                    val basePos = newPositions[entity] ?: entity.pos
                                    newPositions[entity] = basePos.add(TrackedPosition().withDelta(deltaX.toLong(), deltaY.toLong(), deltaZ.toLong()))
                                }
                            }
                        }
                    }

                    is EntityPositionS2CPacket -> {
                        val entity = mc.world?.getEntityById(event.packet.id)
                        if (entity != null)
                            newPositions[entity] = event.packet.let { Vec3d(it.x, it.y, it.z) }
                    }

                    is EntitiesDestroyS2CPacket -> {
                        event.packet.entityIds.forEach {
                            newPositions.remove(mc.world?.getEntityById(it) ?: return@forEach)
                        }
                    }
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
                mode.isSelected(1) || mode.isSelected(3) -> {
                    if (timeUtil.hasReached(pulseDelay.value.toLong())) {
                        onDisable(true)
                        if (!mode.isSelected(3) || shouldPulsate())
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
                        switchState()
                    }
            }
        }

        registerEvent(EventRender3D::class.java) { event ->
            if (affectedPackets.isSelected(1) && !restrictPackets.anySelected()) {
                if (mode.isSelected(3) && !shouldPulsate())
                    return@registerEvent
                newPositions.forEach { (entity, trackedPosition) ->
                    RenderUtil.blockOutline(event.matrices, entity.getDimensions(entity.pose).getBoxAt(trackedPosition), hitBoxColor.getColor().rgb)
                }
            }
        }
    }

    override fun onDisable() {
        onDisable(true)
        packets = CopyOnWriteArrayList() // Force garbage collection
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
