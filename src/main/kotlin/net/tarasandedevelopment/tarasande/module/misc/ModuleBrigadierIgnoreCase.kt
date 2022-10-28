package net.tarasandedevelopment.tarasande.module.misc

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.TextFieldWidget
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueTextList

class ModuleBrigadierIgnoreCase : Module("Brigadier ignore case", "Removes equals() check from brigadier", ModuleCategory.MISC) {

    private val whitelistClientCommands = ValueBoolean(this, "Whitelist client commands", true)
    private val whitelist = ValueTextList(this, "Whitelist", mutableListOf("/", "$"))

    fun isValidCommand(): Boolean {
        if (!enabled) return false

        val children = MinecraftClient.getInstance().currentScreen!!.children()[0] // Input Field for chat messages
        if (children is TextFieldWidget) {
            if (children.text.isEmpty()) return false
            if (children.text.startsWith(TarasandeMain.get().managerModule.get(ModuleCommands::class.java).prefix.value) && whitelistClientCommands.value) {
                return true
            }
            return whitelist.value.any { e -> children.text.startsWith(e) }
        }
        return false
    }
}
