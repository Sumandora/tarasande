package su.mandora.tarasande.module.movement

import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventEntityFlag
import su.mandora.tarasande.event.EventJump
import su.mandora.tarasande.event.EventKeyBindingIsPressed
import su.mandora.tarasande.mixin.accessor.IEntity
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.value.ValueBoolean
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