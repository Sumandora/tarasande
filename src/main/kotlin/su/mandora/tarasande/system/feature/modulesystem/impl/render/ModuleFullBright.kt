package su.mandora.tarasande.system.feature.modulesystem.impl.render

import su.mandora.tarasande.event.impl.EventGamma
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueColor
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleFullBright : Module("Full bright", "Makes everything bright", ModuleCategory.RENDER) {

    private val bright = ValueBoolean(this, "Bright", true)
    private val colorModulation = ValueColor(this, "Color modulation", 0.0, 1.0, 1.0, isEnabled = { !bright.value })

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
                val color = colorModulation.getColor()
                red = (red * color.red / 255.0).toInt()
                blue = (blue * color.blue / 255.0).toInt()
                green = (green * color.green / 255.0).toInt()
            }

            event.color = -0x1000000 or (blue shl 16) or (green shl 8) or red
        }
    }
}