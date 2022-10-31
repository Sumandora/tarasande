package net.tarasandedevelopment.tarasande.features.module.render

import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.value.ValueColor
import net.tarasandedevelopment.tarasande.value.ValueNumberRange

class ModuleFog : Module("Fog", "Changes the fog distance and color", ModuleCategory.RENDER) {

    val distance = ValueNumberRange(this, "Distance", 0.1, 0.1, 1.0, 5.0, 0.1)
    val color = ValueColor(this, "Color", 0.0f, 1.0f, 1.0f)

}