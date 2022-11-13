package net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.player

import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ModuleCategory

class ModuleNoMiningTrace : Module("No mining trace", "Allows you to mine blocks through entities", ModuleCategory.PLAYER) {

    val onlyWhenPickaxe = ValueBoolean(this, "Only when holding pickaxe", true)

}
