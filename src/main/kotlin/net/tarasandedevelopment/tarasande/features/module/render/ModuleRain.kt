package net.tarasandedevelopment.tarasande.features.module.render

import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.value.ValueNumber

class ModuleRain : Module("Rain", "Forces rain at anytime", ModuleCategory.RENDER) {

    val gradient = ValueNumber(this, "Gradient", 0.2, 1.0, 1.0, 0.1)

}