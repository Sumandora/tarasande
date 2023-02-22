package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement

import net.tarasandedevelopment.tarasande.event.impl.EventInput
import net.tarasandedevelopment.tarasande.event.impl.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil

class ModuleSneak : Module("Sneak", "Automatically sneaks", ModuleCategory.MOVEMENT) {

    private val activation = ValueMode(this, "Activation", false, "Manual", "When standing still", "Permanent")

    @Suppress("MemberVisibilityCanBePrivate") // protocol hack package
    val dontSlowdown = ValueBoolean(this, "Don't slowdown", false)

    init {
        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (event.keyBinding == mc.options.sneakKey)
                event.pressed = event.pressed || when {
                    activation.isSelected(0) -> false
                    activation.isSelected(1) -> !PlayerUtil.isPlayerMoving()
                    /*activation.isSelected(2)*/else -> true
                }
        }

        registerEvent(EventInput::class.java) { event ->
            if (event.input == mc.player?.input)
                if (dontSlowdown.value)
                    event.slowDown = false
        }
    }
}
