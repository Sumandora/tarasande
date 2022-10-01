package su.mandora.tarasande.module.render

import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventRainGradient
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleRain : Module("Rain", "Forces rain at anytime", ModuleCategory.RENDER) {

    private val gradient = ValueNumber(this, "Gradient", 0.2, 1.0, 1.0, 0.1)

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventRainGradient) {
            event.gradient = gradient.value.toFloat()
        }
    }

}