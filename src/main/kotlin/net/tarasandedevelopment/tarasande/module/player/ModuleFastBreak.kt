package net.tarasandedevelopment.tarasande.module.player

import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.value.ValueMode
import net.tarasandedevelopment.tarasande.value.ValueNumber

class ModuleFastBreak : Module("Fast break", "Makes blocks break faster", ModuleCategory.PLAYER) {

    private val mode = ValueMode(this, "Mode", false, "Speed up", "Stop early")
    private val speed = ValueNumber(this, "Speed", 0.0, 0.5, 1.0, 0.01)

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                when {
                    mode.isSelected(0) -> {
                        if (mc.interactionManager!!.currentBreakingProgress > 0.0)
                            mc.interactionManager!!.currentBreakingProgress = (mc.interactionManager!!.currentBreakingProgress + speed.value).toFloat()
                    }

                    mode.isSelected(1) -> {
                        if (mc.interactionManager!!.currentBreakingProgress >= speed.value)
                            mc.interactionManager!!.currentBreakingProgress = 1.0F
                    }
                }
            }
        }
    }
}
