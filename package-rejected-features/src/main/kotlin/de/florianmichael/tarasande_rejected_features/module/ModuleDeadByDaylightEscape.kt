package de.florianmichael.tarasande_rejected_features.module

import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleDeadByDaylightEscape : Module("Dead by daylight escape", "Insta-escapes in Gomme's dead by daylight", ModuleCategory.EXPLOIT) {

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.POST) {
                for (i in 0..150) mc.networkHandler?.sendPacket(PlayerInputC2SPacket(if (i % 2 == 0) 1.0f else -1.0f, 0.0f, false, false))
                switchState()
            }
        }
    }
}
