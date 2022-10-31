package net.tarasandedevelopment.tarasande.features.module.misc

import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket
import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventPacket

class ModuleIgnoreResourcePackHash : Module("Ignore resource pack hash", "Validates all server resource pack hashes", ModuleCategory.MISC) {

    init {
        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.RECEIVE && event.packet is ResourcePackSendS2CPacket) {
                event.packet.hash = "" // The client ignores the hash if it is not 40 characters long
            }
        }
    }
}
