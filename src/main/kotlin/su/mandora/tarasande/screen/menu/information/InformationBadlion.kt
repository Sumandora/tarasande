package su.mandora.tarasande.screen.menu.information

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.screen.menu.information.Information
import su.mandora.tarasande.event.EventPacket
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit

class InformationTimers : Information("Badlion", "Timers") {

    var enabled = false
    val list = CopyOnWriteArrayList<Timer>()

    init {
        TarasandeMain.get().managerEvent?.add { event ->
            if (event is EventPacket)
                if (event.type == EventPacket.Type.RECEIVE)
                    if (event.packet is CustomPayloadS2CPacket) {
                        if (event.packet.channel.toString() == "badlion:timers") {
                            try {
                                var data = String(event.packet.data.writtenBytes)
                                val request = data.split("|")[0]
                                data = data.substring(request.length + 1, data.length)
                                when (request) {
                                    "REGISTER" -> enabled = true
                                    "CHANGE_WORLD" -> enabled = false
                                    "REMOVE_ALL_TIMERS" -> {
                                        enabled = false
                                        list.clear()
                                    }

                                    "ADD_TIMER" -> {
                                        val timer = TarasandeMain.get().gson.fromJson(data, Timer::class.java)
                                        list.add(timer)
                                        timer.lastUpdated = System.currentTimeMillis()
                                    }

                                    "REMOVE_TIMER" -> list.removeIf { it.id == TarasandeMain.get().gson.fromJson(data, RemoveRequest::class.java).id }
                                    "UPDATE_TIMER" -> {
                                        val newTimer = TarasandeMain.get().gson.fromJson(data, Timer::class.java)
                                        newTimer.lastUpdated = System.currentTimeMillis()
                                        list.removeIf { it.id == newTimer.id }
                                        list.add(newTimer)
                                    }

                                    "SYNC_TIMERS" -> {
                                        val syncRequest = TarasandeMain.get().gson.fromJson(data, SyncRequest::class.java)
                                        val timer = list.firstOrNull { it.id == syncRequest.id }
                                        timer?.currentTime = syncRequest.time
                                        timer?.lastUpdated = System.currentTimeMillis()
                                    }
                                }
                            } catch (throwable: Throwable) {
                                throwable.printStackTrace()
                            }
                        }
                    } else if (event.packet is PlayerRespawnS2CPacket) {
                        if (event.packet.dimension !== MinecraftClient.getInstance().world?.registryKey) {
                            list.clear()
                            enabled = false
                        }
                    }
        }
    }

    inner class Timer {
        var id: Long? = null
        var name: String? = null
        var item: Item? = null
        var repeating: Boolean? = null
        var time: Long? = null
        var millis: Long? = null
        var currentTime: Long? = null

        var lastUpdated: Long? = null

        inner class Item {
            var type: String? = null
        }

        override fun toString(): String {
            val timeDelta = (System.currentTimeMillis() - lastUpdated!!) / 50f
            val expectedTime = ((currentTime!! - timeDelta) / 20f * 1000f).toLong()
            return "$name " + String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(expectedTime), TimeUnit.MILLISECONDS.toSeconds(expectedTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(expectedTime)));
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
        if (enabled || list.isEmpty()) return null
        return "\n" + list.joinToString("\n")
    }
}