package net.tarasandedevelopment.tarasande.module.movement

import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventUpdate

class ModuleNoJumpCooldown : Module("No jump cooldown", "Removes minecraft's jump cooldown", ModuleCategory.MOVEMENT) {

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.POST)
                mc.player?.jumpingCooldown = 0
        }
    }
}
