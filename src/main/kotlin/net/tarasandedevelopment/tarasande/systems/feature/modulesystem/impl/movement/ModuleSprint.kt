package net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.movement

import net.minecraft.entity.Entity
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.events.EventEntityFlag
import net.tarasandedevelopment.tarasande.events.EventJump
import net.tarasandedevelopment.tarasande.events.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil

class ModuleSprint : Module("Sprint", "Automatically sprints", ModuleCategory.MOVEMENT) {

    private val allowBackwards = object : ValueBoolean(this, "Allow backwards", false) {
        override fun isEnabled() = TarasandeMain.clientValues().correctMovement.isSelected(0)
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
