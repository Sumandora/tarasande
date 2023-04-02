package su.mandora.tarasande_rejected_features.information

import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventConnectServer
import su.mandora.tarasande.event.impl.EventDisconnect
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.screen.informationsystem.Information
import su.mandora.tarasande.util.math.TimeUtil
import java.util.concurrent.ConcurrentHashMap

class InformationLag : Information("Server", "Lag") {
    private val minimumDelay = ValueNumber(this, "Minimum delay", 100.0, 2500.0, 10000.0, 100.0)
    private val lastPacket = TimeUtil()

    init {
        EventDispatcher.apply {
            add(EventPacket::class.java) {
                if (it.type == EventPacket.Type.RECEIVE && it.packet is WorldTimeUpdateS2CPacket) {
                    lastPacket.reset()
                }
            }
            add(EventConnectServer::class.java) {
                lastPacket.reset()
            }
        }
    }

    override fun getMessage(): String? {
        if (lastPacket.hasReached(minimumDelay.value.toLong())) return (System.currentTimeMillis() - lastPacket.time).toString()
        return null
    }
}

class InformationMovements : Information("Server", "Movements") {
    private val maxDelta = ValueNumber(this, "Max delta", 0.0, 0.0, 50.0, 1.0)
    private val acknowledgeTime = ValueNumber(this, "Acknowledge time", 500.0, 5000.0, 50000.0, 500.0)

    private val lastLocations = ConcurrentHashMap<Vec3d, TimeUtil>()
    private var serversideMovements = 0

    init {
        EventDispatcher.add(EventUpdate::class.java) {
            if (it.state == EventUpdate.State.PRE_PACKET) {
                lastLocations[mc.player!!.pos] = TimeUtil()

                for (lastLocation in lastLocations) {
                    if (lastLocation.value.hasReached(acknowledgeTime.value.toLong())) {
                        lastLocations.remove(lastLocation.key)
                    }
                }
            }
        }
        EventDispatcher.add(EventPacket::class.java) { event ->
            if (mc.player == null) return@add
            if (event.type == EventPacket.Type.RECEIVE) {
                if (event.packet is PlayerPositionLookS2CPacket) {
                    val packet = event.packet as PlayerPositionLookS2CPacket
                    if (lastLocations.any { it.key.squaredDistanceTo(packet.x, packet.y, packet.z) >= maxDelta.value * maxDelta.value })
                        serversideMovements++
                    lastLocations.clear()
                }
            }
        }
        EventDispatcher.add(EventDisconnect::class.java) {
            if (it.connection == mc.networkHandler?.connection) {
                lastLocations.clear()
                serversideMovements = 0
            }
        }
    }

    override fun getMessage(): String? {
        if (mc.player == null) return null
        if (serversideMovements == 0) return null
        return serversideMovements.toString()
    }
}
