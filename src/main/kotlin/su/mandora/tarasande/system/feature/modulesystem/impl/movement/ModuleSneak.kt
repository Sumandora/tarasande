package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import su.mandora.tarasande.event.impl.EventInput
import su.mandora.tarasande.event.impl.EventKeyBindingIsPressed
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.player.PlayerUtil

class ModuleSneak : Module("Sneak", "Automatically sneaks", ModuleCategory.MOVEMENT) {

    private val activation = ValueMode(this, "Activation", false, "Manually", "When standing still", "Permanently")

    private val dontSlowdown = ValueBoolean(this, "Don't slowdown", false)

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
