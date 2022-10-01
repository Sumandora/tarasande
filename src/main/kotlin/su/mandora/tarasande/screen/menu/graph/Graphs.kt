package su.mandora.tarasande.screen.menu.graph

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.screen.menu.graph.Graph
import su.mandora.tarasande.event.EventPacket
import su.mandora.tarasande.event.EventPollEvents
import su.mandora.tarasande.event.EventSwing
import net.tarasandedevelopment.tarasande.mixin.accessor.IClientPlayerEntity
import su.mandora.tarasande.event.EventPacketTransform
import su.mandora.tarasande.util.extension.minus
import su.mandora.tarasande.util.render.RenderUtil
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.round

class GraphFPS : Graph("FPS", 200) {

    private val data = ArrayList<Double>()

    init {
        TarasandeMain.get().managerEvent.add {
            if (it is EventPollEvents)
                data.add(RenderUtil.deltaTime)
        }
    }

    override fun supplyData(): Double? {
        if (data.isEmpty())
            return null
        val average = data.average()
        data.clear()
        if (average <= 0.0)
            return null
        return round(1000.0 / average * 10) / 10.0
    }
}

class GraphTPS : Graph("TPS", 200) {

    /* Explanation:
     * A WorldTimeUpdate Packet is sent every 20 ticks (a second)
     * Now we take the time from the last to the current packet
     * and divide by 1000ms that would be the correct answer if the tps would be 1 by default
     * but of course this is wrong means we have to multiply by 20
     * now we got the tps
     * in case the server is a 60 tps server (gamster moment)
     * the packets would come trice as fast
     * resulting in the timeDelta not being 1000ms but 333ms means it is trice the amount
     *
     * Conclusion: it is correct
     */

    private var lastWorldTimePacket = 0L
    private var timeDelta = 0L

    init {
        TarasandeMain.get().managerEvent.add { event ->
            if (event is EventPacket) {
                if (event.type == EventPacket.Type.RECEIVE && event.packet is WorldTimeUpdateS2CPacket) {
                    if (lastWorldTimePacket > 0L) {
                        timeDelta = System.currentTimeMillis() - lastWorldTimePacket
                    }
                    lastWorldTimePacket = System.currentTimeMillis()
                }
            }
        }
    }

    override fun supplyData() = if (timeDelta > 0L) round(timeDelta / 1000.0 * 20 * 100) / 100.0 else null
}

class GraphCPS : Graph("CPS", 200) {

    private val clicks = ArrayList<Long>()

    init {
        TarasandeMain.get().managerEvent.add { event ->
            if (event is EventSwing) {
                clicks.add(System.currentTimeMillis())
            }
        }
    }

    override fun supplyData(): Number {
        clicks.removeIf { click -> System.currentTimeMillis() - click > 1000 }
        return clicks.size
    }
}

class GraphYawDelta : Graph("Yaw Delta", 200) {
    private var lastYaw = 0.0f

    override fun supplyData(): Number? {
        if (MinecraftClient.getInstance().player == null) return null
        val yawDelta = round(((MinecraftClient.getInstance().player as IClientPlayerEntity).tarasande_getLastYaw() - lastYaw) * 100) / 100.0
        lastYaw = (MinecraftClient.getInstance().player as IClientPlayerEntity).tarasande_getLastYaw()
        return yawDelta
    }
}

class GraphPitchDelta : Graph("Pitch Delta", 200) {
    private var lastPitch = 0.0f

    override fun supplyData(): Number? {
        if (MinecraftClient.getInstance().player == null) return null
        val pitchDelta = round(((MinecraftClient.getInstance().player as IClientPlayerEntity).tarasande_getLastPitch() - lastPitch) * 100) / 100.0
        lastPitch = (MinecraftClient.getInstance().player as IClientPlayerEntity).tarasande_getLastPitch()
        return pitchDelta
    }
}

class GraphMotion : Graph("Motion", 200) {
    override fun supplyData(): Number? {
        if (MinecraftClient.getInstance().player == null) return null
        return round((MinecraftClient.getInstance().player?.pos!! - Vec3d(MinecraftClient.getInstance().player?.prevX!!, MinecraftClient.getInstance().player?.prevY!!, MinecraftClient.getInstance().player?.prevZ!!)).horizontalLength() * 100) / 100.0
    }
}

class GraphPing : Graph("Ping", 200) {
    override fun supplyData(): Number? {
        if (MinecraftClient.getInstance().networkHandler != null) {
            if (MinecraftClient.getInstance().networkHandler?.playerList != null) {
                var playerListEntry: PlayerListEntry? = null
                for (entry in MinecraftClient.getInstance().networkHandler?.playerList!!) {
                    if (entry.profile.name.equals(MinecraftClient.getInstance().session.profile.name)) playerListEntry = entry
                }
                if (playerListEntry != null) {
                    return playerListEntry.latency
                }
            }
        }
        return null
    }
}

class GraphOnlinePlayers : Graph("Online Players", 200) {
    override fun supplyData(): Number? {
        if (MinecraftClient.getInstance().networkHandler != null) {
            if (MinecraftClient.getInstance().networkHandler?.playerList != null) {
                return MinecraftClient.getInstance().networkHandler?.playerList?.size
            }
        }
        return null
    }
}

class GraphMemory : Graph("Memory", 200) {
    override fun supplyData() = round((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0 / 1024.0 * 100.0) / 100.0
}

class GraphIncomingTraffic : Graph("Incoming Traffic", 200) {

    private val traffic = CopyOnWriteArrayList<Pair<Long, Int>>()

    init {
        TarasandeMain.get().managerEvent.add { event ->
            if (event is EventPacketTransform && event.type == EventPacketTransform.Type.DECODE) {
                traffic.add(Pair(System.currentTimeMillis(), event.buf!!.readableBytes()))
            }
        }
    }

    override fun supplyData(): Number {
        traffic.removeIf { traffic -> System.currentTimeMillis() - traffic.first > 1000 }
        return traffic.sumOf {
            it.second
        }
    }
}

class GraphOutgoingTraffic : Graph("Outgoing Traffic", 200) {

    private val traffic = CopyOnWriteArrayList<Pair<Long, Int>>()

    init {
        TarasandeMain.get().managerEvent.add { event ->
            if (event is EventPacketTransform && event.type == EventPacketTransform.Type.ENCODE) {
                traffic.add(Pair(System.currentTimeMillis(), event.buf!!.readableBytes()))
            }
        }
    }

    override fun supplyData(): Number {
        traffic.removeIf { traffic -> System.currentTimeMillis() - traffic.first > 1000 }
        return traffic.sumOf {
            it.second
        }
    }
}
