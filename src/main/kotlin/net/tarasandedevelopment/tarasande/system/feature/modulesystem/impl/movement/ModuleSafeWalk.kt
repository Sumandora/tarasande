package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement

import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil

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
            if (event.keyBinding == mc.options.sneakKey && sneak.value && PlayerUtil.isPlayerMoving())
                event.pressed = event.pressed || when {
                    offGround.isSelected(0) -> mc.player?.isOnGround == true && PlayerUtil.isOnEdge(extrapolation.value)
                    offGround.isSelected(1) -> mc.player?.isOnGround == false || PlayerUtil.isOnEdge(extrapolation.value)
                    offGround.isSelected(2) -> PlayerUtil.isOnEdge(extrapolation.value)
                    else -> true
                }
        }
    }
}
