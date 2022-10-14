package net.tarasandedevelopment.tarasande.module.movement

import net.tarasandedevelopment.eventsystem.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueMode
import net.tarasandedevelopment.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleSafeWalk : Module("Safe walk", "Prevents falling off blocks", ModuleCategory.MOVEMENT) {

    val sneak = ValueBoolean(this, "Sneak", false)
    private val offGround = object : ValueMode(this, "Off-ground", false, "Force disable", "Force enable", "Ignore") {
        override fun isEnabled() = sneak.value
    }
    private val extrapolation = object : ValueNumber(this, "Extrapolation", 0.0, 1.0, 10.0, 1.0) {
        override fun isEnabled() = sneak.value
    }

    init {
        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (event.keyBinding == mc.options.sneakKey && sneak.value)
                event.pressed = event.pressed || when {
                    offGround.isSelected(0) -> mc.player?.isOnGround == true && PlayerUtil.isOnEdge(extrapolation.value)
                    offGround.isSelected(1) -> mc.player?.isOnGround == false || PlayerUtil.isOnEdge(extrapolation.value)
                    offGround.isSelected(2) -> PlayerUtil.isOnEdge(extrapolation.value)
                    else -> true
                }
        }
    }

}