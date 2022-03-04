package su.mandora.tarasande.module.render

import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventColorCorrection
import su.mandora.tarasande.value.ValueColor
import java.util.function.Consumer

class ModuleColorCorrection : Module("Color correction", "Corrects colors on the framebuffer", ModuleCategory.RENDER) {

    private val color = ValueColor(this, "Color", 0.0f, 1.0f, 1.0f)

    val eventConsumer = Consumer<Event> { event ->
        if(event is EventColorCorrection) {
            val color = color.getColor()
            event.red = color.red
            event.green = color.green
            event.blue = color.blue
        }
    }

}