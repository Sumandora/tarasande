package de.florianmichael.tarasande.module.player

import net.minecraft.client.MinecraftClient
import net.minecraft.item.PickaxeItem
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.value.ValueBoolean

class ModuleNoMiningTrace : Module("No mining trace", "Allows you to mine blocks through entities", ModuleCategory.PLAYER) {

    private val onlyWhenPickaxe = ValueBoolean(this, "Only when holding Pickaxe", true)

    fun shouldDo(): Boolean {
        if (!this.enabled)
            return false
        if (this.onlyWhenPickaxe.value)
            return MinecraftClient.getInstance().player!!.mainHandStack.item is PickaxeItem || MinecraftClient.getInstance().player!!.offHandStack.item is PickaxeItem
        return true
    }
}
