package su.mandora.tarasande.system.feature.modulesystem.impl.misc

import net.minecraft.client.gui.screen.DownloadingTerrainScreen
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.TrackedPosition
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.ClientConnection
import net.minecraft.network.NetworkState
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket
import net.minecraft.network.packet.c2s.play.PlayPongC2SPacket
import net.minecraft.network.packet.s2c.play.*
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.event.impl.EventPollEvents
import su.mandora.tarasande.event.impl.EventRender3D
import su.mandora.tarasande.event.impl.EventTick
import su.mandora.tarasande.feature.rotation.api.Rotation
import su.mandora.tarasande.injection.accessor.IClientConnection
import su.mandora.tarasande.injection.accessor.ILivingEntity
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.*
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.system.screen.graphsystem.Graph
import su.mandora.tarasande.system.screen.graphsystem.ManagerGraph
import su.mandora.tarasande.util.DEFAULT_REACH
import su.mandora.tarasande.util.math.MathUtil
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande.util.maxReach
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.render.RenderUtil
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class ModuleBlink : Module("Blink", "Delays network packets", ModuleCategory.MISC) {

    private val affectedPackets = ValueMode(this, "Affected packets", true, "Serverbound", "Clientbound")
    private val mode = object : ValueMode(this, "Mode", false, "State-dependent", "Pulse blink", "Latency", "Automatic") {
        override fun onChange(index: Int, oldSelected: Boolean, newSelected: Boolean) = onDisable()
    }
    private val pulseDelay = object : ValueNumber(this, "Pulse delay", 0.0, 500.0, 1000.0, 10.0, isEnabled = { mode.isSelected(1) || mode.isSelected(3) }) {
        override fun onChange(oldValue: Double?, newValue: Double) {
            onDisable()
        }
    }
    private val latency = object : ValueNumber(this, "Latency", 0.0, 500.0, 1000.0, 10.0, isEnabled = { mode.isSelected(2) }) {
        override fun onChange(oldValue: Double?, newValue: Double) {
            onDisable()
        }
    }
    private val reach = ValueNumber(this, "Reach", 0.1, DEFAULT_REACH, maxReach, 0.1, isEnabled = { mode.isSelected(3) })
    private val cancelKey = ValueBind(this, "Cancel key", ValueBind.Type.KEY, GLFW.GLFW_KEY_UNKNOWN, isEnabled = { mode.isSelected(0) && affectedPackets.isSelected(0) })
    private val restrictPackets = ValueMode(this, "Restrict packets", true, "Keep alive", "Ping", isEnabled = { affectedPackets.anySelected() })
    private val hitBoxColor = ValueColor(this, "Hit box color", 0.0, 1.0, 1.0, 1.0, isEnabled = { affectedPackets.isSelected(1) })
    private val ignoreChunks = ValueBoolean(this, "Ignore chunks", true)

    private var packets = CopyOnWriteArrayList<Triple<Packet<*>, EventPacket.Type, Long>>()
    private val timeUtil = TimeUtil()

    private var pos: Vec3d? = null
    private var velocity: Vec3d? = null
    private var rotation: Rotation? = null

    private var newPositions = ConcurrentHashMap<Entity, Vec3d>()

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
        packets = CopyOnWriteArrayList()
        newPositions = ConcurrentHashMap()
    }

    private fun shouldPulsate(): Boolean {
        //@formatter:off
        val validEntities =
            (mc.world?.entities ?: return false).
            asSequence().
            filterIsInstance<PlayerEntity>().
            filter { PlayerUtil.isAttackable(it) }.
            filter { mc.player?.eyePos?.squaredDistanceTo(MathUtil.closestPointToBox(mc.player?.eyePos!!, it.boundingBox.expand(it.targetingMargin.toDouble())))!! <= reach.value * reach.value }.
            toList()
        //@formatter:on
        if (validEntities.isEmpty())
            return false

        return validEntities.all {
            val newPosition = newPositions[it] ?: return true
            val lastPosition = (it as ILivingEntity).tarasande_prevServerPos() ?: return true

            val dimensions = it.getDimensions(it.pose)

            val actualDist = mc.player?.eyePos?.distanceTo(MathUtil.closestPointToBox(mc.player?.eyePos!!, dimensions.getBoxAt(newPosition).expand(it.targetingMargin.toDouble())))!!
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

                // Entity position tracker
                when (event.packet) {
                    is EntityS2CPacket -> {
                        event.packet.apply {
                            if (positionChanged) {
                                val entity = mc.world?.getEntityById(event.packet.id)
                                if (entity != null && entity is LivingEntity) {
                                    val basePos = newPositions[entity] ?: entity.pos
                                    newPositions[entity] = TrackedPosition().also { it.setPos(basePos) }.withDelta(deltaX.toLong(), deltaY.toLong(), deltaZ.toLong())
                                }
                            }
                        }
                    }

                    is EntityPositionS2CPacket -> {
                        val entity = mc.world?.getEntityById(event.packet.id)
                        if (entity != null && entity is LivingEntity)
                            newPositions[entity] = event.packet.let { Vec3d(it.x, it.y, it.z) }
                    }

                    is EntitiesDestroyS2CPacket -> {
                        event.packet.entityIds.forEach {
                            newPositions.remove(mc.world?.getEntityById(it) ?: return@forEach)
                        }
                    }
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
            if(event.state != EventRender3D.State.POST) return@registerEvent

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
            mc.player?.apply {
                setPosition(pos)
                velocity = velocity
                rotation?.applyOn(this)
            }
        }
    }
}
