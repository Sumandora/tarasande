package net.tarasandedevelopment.tarasande.graphsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.*
import net.tarasandedevelopment.tarasande.graphsystem.Graph
import net.tarasandedevelopment.tarasande.util.extension.minus
import net.tarasandedevelopment.tarasande.value.impl.ValueMode
import net.tarasandedevelopment.tarasande.value.impl.ValueNumber
import org.lwjgl.glfw.GLFW
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.round

net.tarasandedevelopment.tarasande.util.string.StringUtil
import net.tarasandedevelopment.tarasande.value.impl.ValueMode
import net.tarasandedevelopment.tarasande.value.impl.ValueNumber
import org.lwjgl.glfw.GLFW
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.round

class GraphFPS : Graph("FPS", 200) {

    private val data = ArrayList<Double>()

    init {
        TarasandeMain.get().managerEvent.add(EventPollEvents::class.java) {
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
    private var timeDeltas = ArrayList<Long>()

    private var average = 0L

    private val samples = ValueNumber(this, "Samples", 1.0, 2.0, 10.0, 1.0)

    init {
        TarasandeMain.get().managerEvent.also {
            it.add(EventPacket::class.java) {
                if (it.type == EventPacket.Type.RECEIVE && it.packet is WorldTimeUpdateS2CPacket) {
                    if (lastWorldTimePacket > 0L) {
                        timeDeltas.add(System.currentTimeMillis() - lastWorldTimePacket)
                        while (timeDeltas.size > samples.value)
                            timeDeltas.removeAt(0)
                        average = timeDeltas.average().toLong()
                    }
                    lastWorldTimePacket = System.currentTimeMillis()
                }
            }

            it.add(EventDisconnect::class.java) {
                lastWorldTimePacket = 0L
                timeDeltas.clear()

                average = 0L
            }
        }
    }

    override fun supplyData() = if (average > 0L) round(average / 1000.0 * 20 * 100) / 100.0 else null
}

class GraphCPS : Graph("CPS", 200) {

    private var clickMode = ValueMode(this, "Click mode", false, "Hand swing", "mouse click")
    private val clicks = ArrayList<Long>()

    init {
        TarasandeMain.get().managerEvent.also {
            it.add(EventSwing::class.java) {
                if (clickMode.isSelected(0))
                    clicks.add(System.currentTimeMillis())
            }
            it.add(EventMouse::class.java) {
                if (it.action == GLFW.GLFW_PRESS && clickMode.isSelected(1))
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
        val yawDelta = round(MinecraftClient.getInstance().player!!.lastYaw * 100) / 100.0
        lastYaw = MinecraftClient.getInstance().player!!.lastYaw
        return yawDelta
    }
}

class GraphPitchDelta : Graph("Pitch Delta", 200) {
    private var lastPitch = 0.0f

    override fun supplyData(): Number? {
        if (MinecraftClient.getInstance().player == null) return null
        val pitchDelta = round(MinecraftClient.getInstance().player!!.lastPitch * 100) / 100.0
        lastPitch = MinecraftClient.getInstance().player!!.lastPitch
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

    private val decimalPlaces = ValueNumber(this, "Decimal places", 1.0, 1.0, 5.0, 1.0)
    private val traffic = CopyOnWriteArrayList<Pair<Long, Int>>()

    init {
        TarasandeMain.get().managerEvent.add(EventPacketTransform::class.java) {
            if (it.type == EventPacketTransform.Type.DECODE) {
                traffic.add(Pair(System.currentTimeMillis(), it.buf!!.readableBytes()))
            }
        }
    }

    override fun supplyData(): Number {
        traffic.removeIf { traffic -> System.currentTimeMillis() - traffic.first > 1000 }
        return traffic.sumOf { it.second }
    }

    override fun formatHud(): String? {
        return StringUtil.formatBytes(this.lastData?.toLong() ?: return null, this.decimalPlaces.value.toInt())
    }
}

class GraphOutgoingTraffic : Graph("Outgoing Traffic", 200) {

    private val decimalPlaces = ValueNumber(this, "Decimal places", 1.0, 1.0, 5.0, 1.0)
    private val traffic = CopyOnWriteArrayList<Pair<Long, Int>>()

    init {
        TarasandeMain.get().managerEvent.add(EventPacketTransform::class.java) {
            if (it.type == EventPacketTransform.Type.ENCODE) {
                traffic.add(Pair(System.currentTimeMillis(), it.buf!!.readableBytes()))
            }
        }
    }

    override fun supplyData(): Number {
        traffic.removeIf { traffic -> System.currentTimeMillis() - traffic.first > 1000 }
        return traffic.sumOf { it.second }
    }


    override fun formatHud(): String? {
        return StringUtil.formatBytes(this.lastData?.toLong() ?: return null, this.decimalPlaces.value.toInt())
    }
}

class GraphTX : Graph("TX", 200) {

    override fun supplyData(): Number? {
        if (MinecraftClient.getInstance().world == null) return null

        return MinecraftClient.getInstance().networkHandler!!.connection.averagePacketsSent.toInt()
    }
}

class GraphRX : Graph("RX", 200) {

    override fun supplyData(): Number? {
        if (MinecraftClient.getInstance().world == null) return null

        return MinecraftClient.getInstance().networkHandler!!.connection.averagePacketsReceived.toInt()
    }
}
