package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player

import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleNoMiningTrace : Module("No mining trace", "Allows you to mine blocks through entities", ModuleCategory.PLAYER) {

    val onlyWhenPickaxe = ValueBoolean(this, "Only when holding pickaxe", true)

}
