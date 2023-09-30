package su.mandora.tarasande_third_party.information

import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventDisconnect
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.gson
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.informationsystem.Information
import su.mandora.tarasande.util.extension.minecraft.asByteArray
import su.mandora.tarasande.util.extension.minecraft.packet.isNewWorld
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit

class InformationTimers : Information("Badlion", "Timers") {

    private var list = CopyOnWriteArrayList<Timer>()
    private var enabled = false

    init {
        EventDispatcher.apply {
            add(EventPacket::class.java) {
                if (it.type == EventPacket.Type.RECEIVE)
                    if (it.packet is CustomPayloadS2CPacket) {
                        val packet = it.packet as CustomPayloadS2CPacket
                        val channel = packet.payload.id()
                        if (channel.toString() == "badlion:timers") {
                            try {
                                var data = PacketByteBuf(Unpooled.buffer()).also { packet.payload.write(it) }.asByteArray().decodeToString()
                                val request = data.split("|")[0]
                                data = data.substring(request.length + 1, data.length)
                                when (request) {
                                    "REGISTER", "CHANGE_WORLD" -> enabled = true
                                    "REMOVE_ALL_TIMERS" -> list = CopyOnWriteArrayList()
                                    "ADD_TIMER" -> {
                                        val timer = gson.fromJson(data, Timer::class.java)
                                        list.add(timer)
                                        timer.lastUpdated = System.currentTimeMillis()
                                    }

                                    "REMOVE_TIMER" -> list.removeIf { timer -> timer.id == gson.fromJson(data, RemoveRequest::class.java).id }
                                    "UPDATE_TIMER" -> {
                                        val newTimer = gson.fromJson(data, Timer::class.java)
                                        newTimer.lastUpdated = System.currentTimeMillis()
                                        list.removeIf { timer -> timer.id == newTimer.id }
                                        list.add(newTimer)
                                    }

                                    "SYNC_TIMERS" -> {
                                        val syncRequest = gson.fromJson(data, SyncRequest::class.java)
                                        val timer = list.firstOrNull { timer -> timer.id == syncRequest.id }
                                        timer?.currentTime = syncRequest.time
                                        timer?.lastUpdated = System.currentTimeMillis()
                                    }
                                }
                            } catch (throwable: Throwable) {
                                throwable.printStackTrace()
                            }
                        }
                    } else if (it.packet is PlayerRespawnS2CPacket) {
                        if ((it.packet as PlayerRespawnS2CPacket).isNewWorld()) {
                            list = CopyOnWriteArrayList()
                            enabled = false
                        }
                    }
            }
            add(EventDisconnect::class.java) {
                if (it.connection == mc.networkHandler?.connection) {
                    list = CopyOnWriteArrayList()
                    enabled = false
                }
            }
        }
    }

    inner class Timer {
        var id: Long? = null
        private var name: String? = null

        @Suppress("unused")
        var item: Item? = null

        @Suppress("MemberVisibilityCanBePrivate")
        var repeating: Boolean? = null

        @Suppress("unused")
        var time: Long? = null

        @Suppress("unused")
        var millis: Long? = null
        var currentTime: Long? = null

        var lastUpdated: Long? = null

        inner class Item {
            @Suppress("unused")
            var type: String? = null
        }

        private fun calcInterpolatedTime(): Long {
            val timeDelta = (System.currentTimeMillis() - lastUpdated!!) / 50F
            return ((currentTime!! - timeDelta) / 20F * 1000F).toLong()
        }

        fun isHidden(): Boolean {
            return !repeating!! && calcInterpolatedTime() < 0L
        }

        override fun toString(): String {
            val expectedTime = calcInterpolatedTime()
            // TODO StringBuilder
            var txt = ""
            if (name != null)
                txt += "$name "
            return txt + String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(expectedTime), TimeUnit.MILLISECONDS.toSeconds(expectedTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(expectedTime)))
        }
    }

    inner class RemoveRequest {
        var id: Long? = null
    }

    inner class SyncRequest {
        var id: Long? = null
        var time: Long? = null
    }

    override fun getMessage(): String? {
        if (!enabled || list.isEmpty()) return null
        return "\n" + list.filter { !it.isHidden() }.joinToString("\n")
    }
}