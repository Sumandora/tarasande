package su.mandora.tarasande.system.feature.modulesystem.impl.render

import su.mandora.tarasande.event.impl.EventFogColor
import su.mandora.tarasande.event.impl.EventFogEnd
import su.mandora.tarasande.event.impl.EventFogStart
import su.mandora.tarasande.system.base.valuesystem.impl.ValueColor
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumberRange
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleFog : Module("Fog", "Changes the fog distance and color", ModuleCategory.RENDER) {

    val distance = ValueNumberRange(this, "Distance", 0.1, 0.1, 1.0, 5.0, 0.1)
    val color = ValueColor(this, "Color", 0.0, 1.0, 1.0)

    init {
        registerEvent(EventFogStart::class.java) { event -> event.distance *= distance.minValue.toFloat() }
        registerEvent(EventFogEnd::class.java) { event -> event.distance *= distance.maxValue.toFloat() }
        registerEvent(EventFogColor::class.java) { event ->
            color.getColor().also {
                event.color[0] = it.red / 255F
                event.color[1] = it.green / 255F
                event.color[2] = it.blue / 255F
            }
        }
    }


}