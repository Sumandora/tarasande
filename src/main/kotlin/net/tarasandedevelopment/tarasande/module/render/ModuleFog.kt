package net.tarasandedevelopment.tarasande.module.render

import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventClearColor
import net.tarasandedevelopment.tarasande.event.EventFogColor
import net.tarasandedevelopment.tarasande.event.EventRenderSky
import net.tarasandedevelopment.tarasande.value.ValueColor
import net.tarasandedevelopment.tarasande.value.ValueNumberRange
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