package net.tarasandedevelopment.tarasande.module.misc

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.TextFieldWidget
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.value.ValueTextList

class ModuleBrigadierIgnoreCase : Module("Brigadier ignore case", "Removes equals() check from brigadier", ModuleCategory.MISC) {

    private val whitelist = ValueTextList(this, "Whitelist", mutableListOf("/", "$"))

    fun isValidCommand(): Boolean {
        if (!enabled) return false

        val children = MinecraftClient.getInstance().currentScreen!!.children()[0]
        if (children is TextFieldWidget) {
            if (children.text.isEmpty()) return false

            whitelist.value.forEach {
                return children.text.startsWith(it)
            }
        }
        return false
    }
}
