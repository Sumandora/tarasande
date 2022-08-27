package su.mandora.tarasande.screen.menu.information

import com.google.common.collect.Iterables
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.screen.menu.information.Information
import su.mandora.tarasande.event.EventPacket
import java.util.function.Consumer

class InformationEntities : Information("World", "Entities") {
    override fun getMessage(): String? {
        if (MinecraftClient.getInstance().world == null)
            return null
        return Iterables.size(MinecraftClient.getInstance().world?.entities!!).toString()
    }
}

class InformationWorldTime : Information("World", "World Time") {

    var lastUpdate: Pair<Long, Long>? = null

    init {
        TarasandeMain.get().managerEvent?.add(Pair(1, Consumer<Event> { event ->
            if (event is EventPacket) {
                if (event.type == EventPacket.Type.RECEIVE && event.packet is WorldTimeUpdateS2CPacket) {
                    lastUpdate = Pair(event.packet.timeOfDay, event.packet.time)
                }
            }
        }))
    }

    override fun getMessage(): String? {
        if (MinecraftClient.getInstance().world == null)
            return null
        if (lastUpdate == null)
            return null
        return lastUpdate?.first.toString() + "/" + lastUpdate?.second
    }
}