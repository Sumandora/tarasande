package su.mandora.tarasande.module.render

import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventClearColor
import su.mandora.tarasande.event.EventFogColor
import su.mandora.tarasande.event.EventRenderSky
import su.mandora.tarasande.value.ValueColor
import su.mandora.tarasande.value.ValueNumberRange
import java.util.function.Consumer

class ModuleFog : Module("Fog", "Changes the fog distance and color", ModuleCategory.RENDER) {

    private val distance = ValueNumberRange(this, "Distance", 0.1, 0.1, 1.0, 5.0, 0.1)
    private val color = ValueColor(this, "Color", 0.0f, 1.0f, 1.0f)

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventFogColor -> {
                val color = color.getColor()
                event.start *= distance.minValue.toFloat()
                event.end *= distance.maxValue.toFloat()
                event.red = color.red / 255.0f
                event.green = color.green / 255.0f
                event.blue = color.blue / 255.0f
            }

            is EventClearColor -> {
                val color = color.getColor()
                event.red = color.red / 255.0f
                event.green = color.green / 255.0f
                event.blue = color.blue / 255.0f
            }

            is EventRenderSky -> {
                event.cancelled = true
            }
        }
    }

}