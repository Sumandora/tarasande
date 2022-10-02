package net.tarasandedevelopment.tarasande.module.movement

import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleSafeWalk : Module("Safe walk", "Prevents falling off blocks", ModuleCategory.MOVEMENT) {

    val sneak = ValueBoolean(this, "Sneak", false)
    private val onlyOnGround = object : ValueBoolean(this, "Only on ground", true) {
        override fun isEnabled() = sneak.value
    }
    private val extrapolation = object : ValueNumber(this, "Extrapolation", 0.0, 1.0, 10.0, 1.0) {
        override fun isEnabled() = sneak.value
    }

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventKeyBindingIsPressed) {
            if (event.keyBinding == mc.options.sneakKey && sneak.value)
                if (!onlyOnGround.value || mc.player?.isOnGround!!)
                    if (PlayerUtil.isOnEdge(extrapolation.value))
                        event.pressed = true
        }
    }

}