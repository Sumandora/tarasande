package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render

import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleRain : Module("Rain", "Forces rain at anytime", ModuleCategory.RENDER) {

    val gradient = ValueNumber(this, "Gradient", 0.2, 1.0, 1.0, 0.1)

}