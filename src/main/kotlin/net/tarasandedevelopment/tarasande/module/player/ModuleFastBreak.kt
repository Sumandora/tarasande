package net.tarasandedevelopment.tarasande.module.player

import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mixin.accessor.IClientPlayerInteractionManager
import net.tarasandedevelopment.tarasande.value.ValueMode
import net.tarasandedevelopment.tarasande.value.ValueNumber

class ModuleFastBreak : Module("Fast break", "Makes blocks break faster", ModuleCategory.PLAYER) {

    private val mode = ValueMode(this, "Mode", false, "Speed up", "Stop early")
    private val speed = ValueNumber(this, "Speed", 0.0, 0.5, 1.0, 0.01)

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                val accessor = mc.interactionManager as IClientPlayerInteractionManager
                when {
                    mode.isSelected(0) -> {
                        if (accessor.tarasande_getCurrentBreakingProgress() > 0.0)
                            accessor.tarasande_setCurrentBreakingProgress((accessor.tarasande_getCurrentBreakingProgress() + speed.value).toFloat())
                    }

                    mode.isSelected(1) -> {
                        if (accessor.tarasande_getCurrentBreakingProgress() >= speed.value)
                            accessor.tarasande_setCurrentBreakingProgress(1.0f)
                    }
                }
            }
        }
    }

}