package net.tarasandedevelopment.tarasande.module.ghost

import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventBoundingBoxOverride
import net.tarasandedevelopment.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleHitBox : Module("Hit box", "Makes enemy hit boxes larger", ModuleCategory.GHOST) {

    private val expand = ValueNumber(this, "Expand", 0.0, 0.0, 1.0, 0.1)

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventBoundingBoxOverride) {
            event.boundingBox = event.boundingBox.expand(expand.value)
        }
    }

}