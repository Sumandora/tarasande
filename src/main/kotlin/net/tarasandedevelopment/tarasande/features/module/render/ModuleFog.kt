package net.tarasandedevelopment.tarasande.features.module.render

import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventFog
import net.tarasandedevelopment.tarasande.value.ValueColor
import net.tarasandedevelopment.tarasande.value.ValueNumberRange

class ModuleFog : Module("Fog", "Changes the fog distance and color", ModuleCategory.RENDER) {

    val distance = ValueNumberRange(this, "Distance", 0.1, 0.1, 1.0, 5.0, 0.1)
    val color = ValueColor(this, "Color", 0.0f, 1.0f, 1.0f)

    init {
        registerEvent(EventFog::class.java) { event ->
            when (event.state) {
                EventFog.State.FOG_START -> event.values[0] *= distance.minValue.toFloat()
                EventFog.State.FOG_END -> event.values[0] *= distance.maxValue.toFloat()
                EventFog.State.FOG_COLOR -> {
                    color.getColor().also {
                        event.values[0] = it.red / 255.0f
                        event.values[1] = it.green / 255.0f
                        event.values[2] = it.blue / 255.0f
                    }
                }
            }
        }
    }


}