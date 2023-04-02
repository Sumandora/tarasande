package su.mandora.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.client.MinecraftClient
import net.minecraft.item.PickaxeItem
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.extension.minecraft.isBlockHitResult

class ModuleNoMiningTrace : Module("No mining trace", "Allows you to mine blocks through entities", ModuleCategory.PLAYER) {

    private val onlyWhenPickaxe = ValueBoolean(this, "Only when holding pickaxe", true)

    fun shouldCancel(): Boolean {
        if (enabled.value)
            if (!onlyWhenPickaxe.value || MinecraftClient.getInstance().player!!.mainHandStack.item is PickaxeItem)
                if (MinecraftClient.getInstance().crosshairTarget.isBlockHitResult())
                    return true
        return false
    }

}
