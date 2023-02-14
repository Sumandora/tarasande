package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.gamemenu

import net.minecraft.client.gui.screen.GameMenuScreen
import net.minecraft.client.network.AllowedAddressResolver
import net.minecraft.client.network.ServerAddress
import net.minecraft.util.Formatting
import net.tarasandedevelopment.tarasande.feature.statusrenderer.StatusRenderer
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList

class ScreenExtensionButtonListGameMenuScreen : ScreenExtensionButtonList<GameMenuScreen>(GameMenuScreen::class.java) {

    init {
        add("Resolve SRV") {
            AllowedAddressResolver.DEFAULT.resolve(ServerAddress.parse(mc.currentServerEntry?.address ?: "")).map { it.inetSocketAddress }.apply {
                if (isPresent) {
                    mc.keyboard.clipboard = get().hostName + ":" + get().port
                    StatusRenderer.setStatus(mc.currentScreen!!, Formatting.GREEN.toString() + "Successfully resolved SRV and copied to clipboard!")
                    return@apply
                }
                StatusRenderer.setStatus(mc.currentScreen!!, Formatting.RED.toString() + "Failed to resolve SRV")
            }
        }
    }
}
