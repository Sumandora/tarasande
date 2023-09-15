package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import su.mandora.tarasande.event.impl.EventInput
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.DEFAULT_SNEAK_SLOWDOWN
import su.mandora.tarasande.util.player.PlayerUtil

class ModuleSneak : Module("Sneak", "Automatically sneaks", ModuleCategory.MOVEMENT) {

    private val activation = ValueMode(this, "Activation", false, "Manually", "When standing still", "Permanently")

    private val changeSpeed = ValueBoolean(this, "Change speed", false)
    private val mode = ValueMode(this, "Mode", false, "Absolute", "Relative", isEnabled = { changeSpeed.value })
    private val amount = ValueNumber(this, "Amount", 0.0, DEFAULT_SNEAK_SLOWDOWN, 1.0, 0.01, isEnabled = { changeSpeed.value })

    fun getModifiedSpeed(f: Float): Float {
        if(!changeSpeed.value)
            return f

        return when {
            mode.isSelected(0) -> amount.value.toFloat()
            mode.isSelected(1) -> f + amount.value.toFloat()
            else -> 0F
        }
    }

    init {
        registerEvent(EventInput::class.java) { event ->
            if (mc.player?.input == event.input)
                event.input.sneaking = event.input.sneaking || when {
                    activation.isSelected(0) -> false
                    activation.isSelected(1) -> !PlayerUtil.isPlayerMoving()
                    activation.isSelected(2) -> true
                    else -> error("Invalid state")
                }
        }
    }
}
