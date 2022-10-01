package net.tarasandedevelopment.tarasande.module.movement

import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventEntityFlag
import net.tarasandedevelopment.tarasande.event.EventJump
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.mixin.accessor.IEntity
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import java.util.function.Consumer

class ModuleSprint : Module("Sprint", "Automatically sprints", ModuleCategory.MOVEMENT) {

    private val allowBackwards = object : ValueBoolean(this, "Allow backwards", false) {
        override fun isEnabled() = TarasandeMain.get().clientValues.correctMovement.isSelected(0)
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventKeyBindingIsPressed -> {
                if (event.keyBinding == mc.options?.sprintKey)
                    event.pressed = true
            }

            is EventEntityFlag -> {
                if (event.entity == mc.player && allowBackwards.isEnabled() && allowBackwards.value)
                    if (event.flag == (event.entity as IEntity).tarasande_getSprintingFlagIndex())
                        if (PlayerUtil.isPlayerMoving()) {
                            mc.player?.isSprinting = true
                            if (mc.player?.input?.jumping == false)
                                event.enabled = false // don't ask
                        }
            }

            is EventJump -> {
                if (event.state == EventJump.State.PRE && allowBackwards.isEnabled() && allowBackwards.value) {
                    mc.player?.isSprinting = true
                    event.yaw = PlayerUtil.getMoveDirection().toFloat()
                }
            }
        }
    }

}