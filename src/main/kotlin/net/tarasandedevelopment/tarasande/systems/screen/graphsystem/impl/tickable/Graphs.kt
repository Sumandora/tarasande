package net.tarasandedevelopment.tarasande.systems.screen.graphsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.event.*
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.Graph
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.GraphTickable
import net.tarasandedevelopment.tarasande.util.extension.minus
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import org.lwjgl.glfw.GLFW
import su.mandora.event.EventDispatcher
import java.util.concurrent.CopyOnWriteArrayList

class GraphFPS : GraphTickable("FPS", 200, true) {

    private val data = ArrayList<Long>()

    init {
        EventDispatcher.add(EventPollEvents::class.java) {
            data.removeIf { System.currentTimeMillis() - it > 1000L }
            data.add(System.currentTimeMillis())
        }
    }

    override fun tick(): Number {
        return data.size.toDouble()
    }
}

class GraphCPS : GraphTickable("CPS", 200, true) {

    private var clickMode = ValueMode(this, "Click mode", false, "Hand swing", "mouse click")
    private val clicks = ArrayList<Long>()

    init {
        EventDispatcher.apply {
            add(EventSwing::class.java) {
                if (clickMode.isSelected(0))
                    clicks.add(System.currentTimeMillis())
            }
            add(EventMouse::class.java) {
                if (it.action == GLFW.GLFW_PRESS && clickMode.isSelected(1))
                    clicks.add(System.currentTimeMillis())
            }
        }
    }

    override fun tick(): Number {
        clicks.removeIf { click -> System.currentTimeMillis() - click > 1000 }
        return clicks.size
    }
}

class GraphYawDelta : Graph("Yaw Delta", 200, false) {
    private var lastYaw = 0.0f

    init {
        EventDispatcher.add(EventUpdate::class.java) { event ->
            if(event.state == EventUpdate.State.POST) {
                val lastYaw = MinecraftClient.getInstance().player?.lastPitch ?: return@add
                add(this.lastYaw - lastYaw)
                this.lastYaw = lastYaw
            }
        }
    }
}

class GraphPitchDelta : Graph("Pitch Delta", 200, false) {
    private var lastPitch = 0.0f

    init {
        EventDispatcher.add(EventUpdate::class.java) { event ->
            if(event.state == EventUpdate.State.POST) {
                val lastPitch = MinecraftClient.getInstance().player?.lastPitch ?: return@add
                add(this.lastPitch - lastPitch)
                this.lastPitch = lastPitch
            }
        }
    }
}

class GraphMotion : GraphTickable("Motion", 200, false) {
    override fun tick(): Number? {
        if (MinecraftClient.getInstance().player == null) return null
        return (MinecraftClient.getInstance().player?.pos!! - Vec3d(MinecraftClient.getInstance().player?.prevX!!, MinecraftClient.getInstance().player?.prevY!!, MinecraftClient.getInstance().player?.prevZ!!)).horizontalLength()
    }
}

class GraphPing : GraphTickable("Ping", 200, true) {
    override fun tick(): Number? {
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

class GraphOnlinePlayers : GraphTickable("Online Players", 200, true) {
    override fun tick(): Number? {
        if (MinecraftClient.getInstance().networkHandler != null) {
            if (MinecraftClient.getInstance().networkHandler?.playerList != null) {
                return MinecraftClient.getInstance().networkHandler?.playerList?.size
            }
        }
        return null
    }
}

class GraphMemory : GraphTickable("Memory", 200, false) {
    override fun tick() = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()

    override fun format(num: Number?): String? {
        return StringUtil.formatBytes(num?.toLong() ?: return null, decimalPlaces)
    }
}

class GraphIncomingTraffic : GraphTickable("Incoming Traffic", 200, true) {

    private val traffic = CopyOnWriteArrayList<Pair<Long, Int>>()

    init {
        EventDispatcher.add(EventPacketTransform::class.java) {
            if (it.type == EventPacketTransform.Type.DECODE) {
                traffic.add(Pair(System.currentTimeMillis(), it.buf!!.readableBytes()))
            }
        }
    }

    override fun tick(): Number {
        traffic.removeIf { traffic -> System.currentTimeMillis() - traffic.first > 1000 }
        return traffic.sumOf { it.second }
    }

    override fun format(num: Number?): String? {
        return StringUtil.formatBytes(num?.toLong() ?: return null, decimalPlaces)
    }
}

class GraphOutgoingTraffic : GraphTickable("Outgoing Traffic", 200, true) {

    private val traffic = CopyOnWriteArrayList<Pair<Long, Int>>()

    init {
        EventDispatcher.add(EventPacketTransform::class.java) {
            if (it.type == EventPacketTransform.Type.ENCODE) {
                traffic.add(Pair(System.currentTimeMillis(), it.buf!!.readableBytes()))
            }
        }
    }

    override fun tick(): Number {
        traffic.removeIf { traffic -> System.currentTimeMillis() - traffic.first > 1000 }
        return traffic.sumOf { it.second }
    }

    override fun format(num: Number?): String? {
        return StringUtil.formatBytes(num?.toLong() ?: return null, decimalPlaces)
    }
}

class GraphTX : GraphTickable("TX", 200, true) {

    override fun tick(): Number? {
        if (MinecraftClient.getInstance().world == null) return null

        return MinecraftClient.getInstance().networkHandler!!.connection.averagePacketsSent.toInt()
    }
}

class GraphRX : GraphTickable("RX", 200, true) {

    override fun tick(): Number? {
        if (MinecraftClient.getInstance().world == null) return null

        return MinecraftClient.getInstance().networkHandler!!.connection.averagePacketsReceived.toInt()
    }
}
