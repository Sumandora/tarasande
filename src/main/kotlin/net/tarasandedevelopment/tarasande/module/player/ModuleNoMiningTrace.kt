package net.tarasandedevelopment.tarasande.module.player

import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.value.ValueBoolean

class ModuleNoMiningTrace : Module("No mining trace", "Allows you to mine blocks through entities", ModuleCategory.PLAYER) {

    val onlyWhenPickaxe = ValueBoolean(this, "Only when holding pickaxe", true)

}
