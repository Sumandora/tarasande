package su.mandora.tarasande.module.player

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventPacket
import java.util.function.Consumer

class ModuleNoHunger : Module("No hunger", "Prevents sprinting packets", ModuleCategory.PLAYER) {

    override fun onEnable() {
        if(mc.player?.isSprinting == true) {
            mc.networkHandler?.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING))
        }
    }

    override fun onDisable() {
        if(mc.player?.isSprinting == false) {
            mc.networkHandler?.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING))
        }
    }

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventPacket)
            if(event.type == EventPacket.Type.SEND)
                if(event.packet is ClientCommandC2SPacket)
                    if(event.packet.mode == ClientCommandC2SPacket.Mode.STOP_SPRINTING || event.packet.mode == ClientCommandC2SPacket.Mode.START_SPRINTING)
                        event.cancelled = true
    }

}