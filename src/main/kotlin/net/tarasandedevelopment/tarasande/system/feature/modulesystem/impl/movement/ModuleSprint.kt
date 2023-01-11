package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement

import net.tarasandedevelopment.tarasande.event.EventJump
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.feature.clientvalue.impl.RotationValues
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.extension.mc
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil

class ModuleSprint : Module("Sprint", "Automatically sprints", ModuleCategory.MOVEMENT) {

    val allowBackwards = object : ValueBoolean(this, "Allow backwards", false) {
        override fun isEnabled() = !RotationValues.correctMovement.isSelected(1)
    }

    init {
        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (event.keyBinding == mc.options?.sprintKey)
                event.pressed = true
        }

        registerEvent(EventJump::class.java) { event ->
            if (event.state == EventJump.State.PRE && allowBackwards.isEnabled() && allowBackwards.value) {
                event.yaw = PlayerUtil.getMoveDirection().toFloat()
            }
        }
    }
}
