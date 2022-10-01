package net.tarasandedevelopment.tarasande.module.render

import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventRainGradient
import net.tarasandedevelopment.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleRain : Module("Rain", "Forces rain at anytime", ModuleCategory.RENDER) {

    private val gradient = ValueNumber(this, "Gradient", 0.2, 1.0, 1.0, 0.1)

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventRainGradient) {
            event.gradient = gradient.value.toFloat()
        }
    }

}