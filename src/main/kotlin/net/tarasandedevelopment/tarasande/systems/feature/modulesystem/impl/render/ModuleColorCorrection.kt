package net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.render

import net.tarasandedevelopment.tarasande.events.EventColorCorrection
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueColor
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ModuleCategory

class ModuleColorCorrection : Module("Color correction", "Corrects colors on the framebuffer", ModuleCategory.RENDER) {

    private val color = ValueColor(this, "Color", 0.0f, 1.0f, 1.0f)

    init {
        registerEvent(EventColorCorrection::class.java) { event ->
            val color = color.getColor()
            event.red = color.red
            event.green = color.green
            event.blue = color.blue
        }
    }
}