package su.mandora.tarasande.system.feature.modulesystem.impl.render

import su.mandora.tarasande.event.impl.EventColorCorrection
import su.mandora.tarasande.system.base.valuesystem.impl.ValueColor
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleColorCorrection : Module("Color correction", "Corrects colors on the framebuffer", ModuleCategory.RENDER) {

    private val color = ValueColor(this, "Color", 0.0, 1.0, 1.0)

    init {
        registerEvent(EventColorCorrection::class.java) { event ->
            val color = color.getColor()
            event.red = color.red
            event.green = color.green
            event.blue = color.blue
        }
    }
}