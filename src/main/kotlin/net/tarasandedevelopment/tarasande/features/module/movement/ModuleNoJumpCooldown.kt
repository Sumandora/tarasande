package net.tarasandedevelopment.tarasande.features.module.movement

import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventUpdate

class ModuleNoJumpCooldown : Module("No jump cooldown", "Removes minecraft's jump cooldown", ModuleCategory.MOVEMENT) {

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.POST)
                mc.player?.jumpingCooldown = 0
        }
    }
}
