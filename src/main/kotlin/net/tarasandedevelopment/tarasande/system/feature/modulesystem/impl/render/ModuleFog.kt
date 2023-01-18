package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render

import net.tarasandedevelopment.tarasande.event.EventFogColor
import net.tarasandedevelopment.tarasande.event.EventFogEnd
import net.tarasandedevelopment.tarasande.event.EventFogStart
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueColor
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumberRange
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleFog : Module("Fog", "Changes the fog distance and color", ModuleCategory.RENDER) {

    val distance = ValueNumberRange(this, "Distance", 0.1, 0.1, 1.0, 5.0, 0.1)
    val color = ValueColor(this, "Color", 0.0, 1.0, 1.0)

    init {
        registerEvent(EventFogStart::class.java) { event -> event.distance *= distance.minValue.toFloat() }
        registerEvent(EventFogEnd::class.java) { event -> event.distance *= distance.minValue.toFloat() }
        registerEvent(EventFogColor::class.java) { event ->
            color.getColor().also {
                event.color[0] = it.red / 255.0F
                event.color[1] = it.green / 255.0F
                event.color[2] = it.blue / 255.0F
            }
        }
    }


}