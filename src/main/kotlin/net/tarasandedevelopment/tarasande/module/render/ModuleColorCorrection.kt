package net.tarasandedevelopment.tarasande.module.render

import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventColorCorrection
import net.tarasandedevelopment.tarasande.value.ValueColor
import java.util.function.Consumer

class ModuleColorCorrection : Module("Color correction", "Corrects colors on the framebuffer", ModuleCategory.RENDER) {

    private val color = ValueColor(this, "Color", 0.0f, 1.0f, 1.0f)

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventColorCorrection) {
            val color = color.getColor()
            event.red = color.red
            event.green = color.green
            event.blue = color.blue
        }
    }

}