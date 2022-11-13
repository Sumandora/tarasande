package net.tarasandedevelopment.tarasande.systems.screen.informationsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
import su.mandora.event.EventDispatcher
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.systems.screen.informationsystem.Information
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit

class InformationTimers : Information("Badlion", "Timers") {

    var enabled = false
    val list = CopyOnWriteArrayList<Timer>()

    init {
        EventDispatcher.apply {
            add(EventPacket::class.java) {
                if (it.type == EventPacket.Type.RECEIVE)
                    if (it.packet is CustomPayloadS2CPacket) {
                        val packet = it.packet
                        if (packet.channel.toString() == "badlion:timers") {
                            try {
                                var data = packet.data.writtenBytes.decodeToString()
                                val request = data.split("|")[0]
                                data = data.substring(request.length + 1, data.length)
                                when (request) {
                                    "REGISTER", "CHANGE_WORLD" -> enabled = true
                                    "REMOVE_ALL_TIMERS" -> list.clear()
                                    "ADD_TIMER" -> {
                                        val timer = TarasandeMain.instance.gson.fromJson(data, Timer::class.java)
                                        list.add(timer)
                                        timer.lastUpdated = System.currentTimeMillis()
                                    }

                                    "REMOVE_TIMER" -> list.removeIf { it.id == TarasandeMain.instance.gson.fromJson(data, RemoveRequest::class.java).id }
                                    "UPDATE_TIMER" -> {
                                        val newTimer = TarasandeMain.instance.gson.fromJson(data, Timer::class.java)
                                        newTimer.lastUpdated = System.currentTimeMillis()
                                        list.removeIf { it.id == newTimer.id }
                                        list.add(newTimer)
                                    }

                                    "SYNC_TIMERS" -> {
                                        val syncRequest = TarasandeMain.instance.gson.fromJson(data, SyncRequest::class.java)
                                        val timer = list.firstOrNull { it.id == syncRequest.id }
                                        timer?.currentTime = syncRequest.time
                                        timer?.lastUpdated = System.currentTimeMillis()
                                    }
                                }
                            } catch (throwable: Throwable) {
                                throwable.printStackTrace()
                            }
                        }
                    } else if (it.packet is PlayerRespawnS2CPacket) {
                        if (MinecraftClient.getInstance().world != null && it.packet.dimension != MinecraftClient.getInstance().world?.registryKey) {
                            list.clear()
                            enabled = false
                        }
                    }
            }
            add(EventDisconnect::class.java) {
                list.clear()
                enabled = false
            }
        }
    }

    inner class Timer {
        var id: Long? = null
        var name: String? = null
        var item: Item? = null

        @Suppress("MemberVisibilityCanBePrivate")
        var repeating: Boolean? = null
        var time: Long? = null

        @Suppress("unused")
        var millis: Long? = null
        var currentTime: Long? = null

        var lastUpdated: Long? = null

        inner class Item {
            var type: String? = null
        }

        private fun calcInterpolatedTime(): Long {
            val timeDelta = (System.currentTimeMillis() - lastUpdated!!) / 50f
            return ((currentTime!! - timeDelta) / 20f * 1000f).toLong()
        }

        internal fun isHidden(): Boolean {
            return !repeating!! && calcInterpolatedTime() < 0L
        }

        override fun toString(): String {
            val expectedTime = calcInterpolatedTime()
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