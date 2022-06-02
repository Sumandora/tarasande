package su.mandora.tarasande.module.movement

import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventKeyBindingIsPressed
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleSafeWalk : Module("Safe walk", "Prevents falling off blocks", ModuleCategory.MOVEMENT) {

    private val sneak = ValueBoolean(this, "Sneak", false)
    private val onlyOnGround = object : ValueBoolean(this, "Only on ground", true) {
        override fun isEnabled() = sneak.value
    }
    private val extrapolation = object : ValueNumber(this, "Extrapolation", 0.0, 1.0, 10.0, 1.0) {
        override fun isEnabled() = sneak.value
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventKeyBindingIsPressed -> {
                if (event.keyBinding == mc.options.sneakKey && sneak.value)
                    if (!onlyOnGround.value || mc.player?.isOnGround!!)
                        if (PlayerUtil.isOnEdge(extrapolation.value))
                            event.pressed = true
            }
        }
    }

}