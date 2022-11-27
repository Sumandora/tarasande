package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render

import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleAntiParticleHide : Module("Anti particle hide", "Makes invisible effects visible", ModuleCategory.RENDER) {

    val inventory = ValueBoolean(this, "Inventory", true)
    val hud = ValueBoolean(this, "HUD", true)
}
