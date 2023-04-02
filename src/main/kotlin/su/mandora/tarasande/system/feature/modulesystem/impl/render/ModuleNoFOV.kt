package su.mandora.tarasande.system.feature.modulesystem.impl.render

import su.mandora.tarasande.event.impl.EventFovMultiplier
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumberRange
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.player.PlayerUtil

class ModuleNoFOV : Module("No FOV", "Limits the dynamic FOV", ModuleCategory.RENDER) {

    private val limit = ValueNumberRange(this, "Limit", 0.1, 1.0, 1.15, 1.5, 0.01)
    private val resetWhenIdling = ValueBoolean(this, "Reset when idling", true)

    init {
        registerEvent(EventFovMultiplier::class.java) { event ->
            if (resetWhenIdling.value && !(PlayerUtil.isPlayerMoving() && mc.player?.isSprinting == true))
                event.movementFovMultiplier = 1.0F
            else
                event.movementFovMultiplier = event.movementFovMultiplier.coerceIn(limit.minValue.toFloat(), limit.maxValue.toFloat())
        }
    }

}