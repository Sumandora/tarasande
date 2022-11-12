package net.tarasandedevelopment.tarasande.features.module.movement

import net.minecraft.entity.Entity
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventEntityFlag
import net.tarasandedevelopment.tarasande.event.EventJump
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.value.impl.ValueBoolean

class ModuleSprint : Module("Sprint", "Automatically sprints", ModuleCategory.MOVEMENT) {

    private val allowBackwards = object : ValueBoolean(this, "Allow backwards", false) {
        override fun isEnabled() = TarasandeMain.get().clientValues.correctMovement.isSelected(0)
    }

    init {
        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (event.keyBinding == mc.options?.sprintKey)
                event.pressed = true
        }

        registerEvent(EventEntityFlag::class.java) { event ->
            if (event.entity == mc.player && allowBackwards.isEnabled() && allowBackwards.value)
                if (event.flag == Entity.SPRINTING_FLAG_INDEX)
                    if (PlayerUtil.isPlayerMoving()) {
                        mc.player?.isSprinting = true
                        if (mc.player?.input?.jumping == false)
                            event.enabled = false // don't ask
                    }
        }

        registerEvent(EventJump::class.java) { event ->
            if (event.state == EventJump.State.PRE && allowBackwards.isEnabled() && allowBackwards.value) {
                mc.player?.isSprinting = true
                event.yaw = PlayerUtil.getMoveDirection().toFloat()
            }
        }
    }
}
