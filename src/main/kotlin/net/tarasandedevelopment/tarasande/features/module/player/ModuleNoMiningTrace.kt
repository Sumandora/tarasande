package net.tarasandedevelopment.tarasande.features.module.player

import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.value.ValueBoolean

class ModuleNoMiningTrace : Module("No mining trace", "Allows you to mine blocks through entities", ModuleCategory.PLAYER) {

    val onlyWhenPickaxe = ValueBoolean(this, "Only when holding pickaxe", true)

}
