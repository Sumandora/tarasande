package su.mandora.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleXCarry : Module("X-Carry", "Expands inventory space", ModuleCategory.PLAYER) {

    init {
        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.SEND) {
                if (event.packet is CloseHandledScreenC2SPacket && event.packet.syncId == 0)
                    event.cancelled = true
            }
        }
    }
}