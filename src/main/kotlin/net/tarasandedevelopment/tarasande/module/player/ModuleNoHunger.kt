package net.tarasandedevelopment.tarasande.module.player

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import net.tarasandedevelopment.eventsystem.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventPacket
import java.util.function.Consumer

class ModuleNoHunger : Module("No hunger", "Prevents sprinting packets", ModuleCategory.PLAYER) {

    override fun onEnable() {
        if (mc.player?.isSprinting == true) {
            mc.networkHandler?.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING))
        }
    }

    override fun onDisable() {
        if (mc.player?.isSprinting == false) {
            mc.networkHandler?.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING))
        }
    }

    init {
        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.SEND)
                if (event.packet is ClientCommandC2SPacket)
                    if (event.packet.mode == ClientCommandC2SPacket.Mode.STOP_SPRINTING || event.packet.mode == ClientCommandC2SPacket.Mode.START_SPRINTING)
                        event.cancelled = true
        }
    }
}