package su.mandora.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleNoHunger : Module("No hunger", "Prevent serverside sprinting", ModuleCategory.PLAYER) {

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
