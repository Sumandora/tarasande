package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import su.mandora.tarasande.event.impl.EventJump
import su.mandora.tarasande.event.impl.EventKeyBindingIsPressed
import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.player.PlayerUtil

class ModuleSprint : Module("Sprint", "Automatically sprints", ModuleCategory.MOVEMENT) {

    val allowBackwards = ValueBoolean(this, "Allow backwards", false, isEnabled = { !Rotations.correctMovement.isSelected(1) })
    val ignoreHunger = ValueBoolean(this, "Ignore hunger", false)

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
