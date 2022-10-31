package net.tarasandedevelopment.tarasande.features.module.render

import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventColorCorrection
import net.tarasandedevelopment.tarasande.value.ValueColor

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