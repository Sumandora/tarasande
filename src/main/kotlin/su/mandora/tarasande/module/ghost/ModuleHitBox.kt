package su.mandora.tarasande.module.ghost

import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventBoundingBoxOverride
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleHitBox : Module("Hit box", "Makes enemy hit boxes larger", ModuleCategory.GHOST) {

    private val expand = ValueNumber(this, "Expand", 0.0, 0.0, 1.0, 0.1)

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventBoundingBoxOverride) {
            event.boundingBox = event.boundingBox.expand(expand.value)
        }
    }

}