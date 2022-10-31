package net.tarasandedevelopment.tarasande.features.module.render

import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventGamma
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueColor

class ModuleFullBright : Module("Full bright", "Makes everything bright", ModuleCategory.RENDER) {

    private val bright = ValueBoolean(this, "Bright", true)
    private val colorModulation = object : ValueColor(this, "Color modulation", 0.0f, 1.0f, 1.0f) {
        override fun isEnabled() = !bright.value
    }

    init {
        registerEvent(EventGamma::class.java) { event ->
            var red = event.color shr 16 and 255
            var green = event.color shr 8 and 255
            var blue = event.color shr 0 and 255

            if (bright.value || (event.x == 15 && event.y == 15)) { // Minecraft code is on another level bro
                blue = 255
                green = 255
                red = 255
            } else {
                /*
                 * Yes the fact that blue and red seem to be inverted is intentional
                 * I don't understand why this is the case, but it seems like minecraft is actually inverting the colors?
                 */
                val color = colorModulation.getColor()
                red = (red * color.red / 255.0).toInt()
                blue = (blue * color.blue / 255.0).toInt()
                green = (green * color.green / 255.0).toInt()
            }

            event.color = -0x1000000 or (blue shl 16) or (green shl 8) or red
        }
    }
}