package su.mandora.tarasande.module.render

import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.value.ValueNumber

class ModuleRain : Module("Rain", "Forces rain at anytime", ModuleCategory.RENDER) {

    val gradient = ValueNumber(this, "Gradient", 0.0, 1.0, 1.0, 0.1)

}