package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render

import net.tarasandedevelopment.tarasande.event.EventFog
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueColor
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumberRange
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleFog : Module("Fog", "Changes the fog distance and color", ModuleCategory.RENDER) {

    val distance = ValueNumberRange(this, "Distance", 0.1, 0.1, 1.0, 5.0, 0.1)
    val color = ValueColor(this, "Color", 0.0F, 1.0F, 1.0F)

    init {
        registerEvent(EventFog::class.java) { event ->
            when (event.state) {
                EventFog.State.FOG_START -> event.values[0] *= distance.minValue.toFloat()
                EventFog.State.FOG_END -> event.values[0] *= distance.maxValue.toFloat()
                EventFog.State.FOG_COLOR -> {
                    color.getColor().also {
                        event.values[0] = it.red / 255.0F
                        event.values[1] = it.green / 255.0F
                        event.values[2] = it.blue / 255.0F
                    }
                }
            }
        }
    }


}