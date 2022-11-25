package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render

import net.tarasandedevelopment.tarasande.event.EventColorCorrection
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueColor
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleColorCorrection : Module("Color correction", "Corrects colors on the framebuffer", ModuleCategory.RENDER) {

    private val color = ValueColor(this, "Color", 0.0F, 1.0F, 1.0F)

    init {
        registerEvent(EventColorCorrection::class.java) { event ->
            val color = color.getColor()
            event.red = color.red
            event.green = color.green
            event.blue = color.blue
        }
    }
}