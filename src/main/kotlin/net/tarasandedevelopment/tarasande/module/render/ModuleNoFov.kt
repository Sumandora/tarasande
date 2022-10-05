package net.tarasandedevelopment.tarasande.module.render

import net.minecraft.util.math.MathHelper
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventMovementFovMultiplier
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueNumberRange
import java.util.function.Consumer

class ModuleNoFov : Module("No Fov", "Limits the dynamic fov", ModuleCategory.RENDER) {

    private val limit = ValueNumberRange(this, "Limit", 0.1, 1.0, 1.15, 1.5, 0.01)
    private val resetWhenIdling = ValueBoolean(this, "Reset when idling", true)

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventMovementFovMultiplier) {
            if (resetWhenIdling.value && !(PlayerUtil.isPlayerMoving() && mc.player?.isSprinting == true))
                event.movementFovMultiplier = 1.0f
            else
                event.movementFovMultiplier = MathHelper.clamp(event.movementFovMultiplier, limit.minValue.toFloat(), limit.maxValue.toFloat())
        }
    }

}