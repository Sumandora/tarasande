package net.tarasandedevelopment.tarasande.screen.cheatmenu.information

import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.information.Information
import net.tarasandedevelopment.tarasande.value.ValueBoolean

class InformationServerBrand : Information("Server", "Server Brand") {

    private val compiledRegex = Regex("\\(.*?\\) ")
    private val regex = ValueBoolean(this, "Regex", true)

    override fun getMessage(): String? {
        if (!MinecraftClient.getInstance().isInSingleplayer) {
            var brand: String? = MinecraftClient.getInstance().player!!.serverBrand ?: return null

            if (regex.value)
                brand = brand!!.replace(compiledRegex, "")
            return brand
        }
        return null
    }
}
