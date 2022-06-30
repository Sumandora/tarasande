package su.mandora.tarasande.module.render

import net.minecraft.util.math.MathHelper
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventMovementFovMultiplier
import su.mandora.tarasande.value.ValueNumberRange
import java.util.function.Consumer

class ModuleNoFov : Module("No Fov", "Limits the dynamic fov", ModuleCategory.RENDER) {

    private val limit = ValueNumberRange(this, "Limit", 0.1, 1.0, 1.5, 1.5, 0.1)

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventMovementFovMultiplier) event.movementFovMultiplier = MathHelper.clamp(event.movementFovMultiplier, limit.minValue.toFloat(), limit.maxValue.toFloat())
    }

}