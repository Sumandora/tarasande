package de.florianmichael.tarasande_serverpinger.screenextension.directconnect

import net.minecraft.client.gui.screen.DirectConnectScreen
import net.minecraft.client.network.AllowedAddressResolver
import net.minecraft.client.network.ServerAddress
import net.minecraft.util.Formatting
import net.tarasandedevelopment.tarasande.feature.statusrenderer.StatusRenderer
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList

class ScreenExtensionButtonListDirectConnect : ScreenExtensionButtonList<DirectConnectScreen>(DirectConnectScreen::class.java) {

    init {
        add("Resolve SRV") {
            val current = mc.currentScreen as DirectConnectScreen

            AllowedAddressResolver.DEFAULT.resolve(ServerAddress.parse(current.addressField.text)).map { it.inetSocketAddress }.apply {
                if (isPresent) {
                    mc.keyboard.clipboard = get().hostName + ":" + get().port
                    StatusRenderer.setStatus(current, Formatting.GREEN.toString() + "Successfully resolved SRV and copied to clipboard!")
                    return@apply
                }
                StatusRenderer.setStatus(current, Formatting.RED.toString() + "Failed to resolve SRV")
            }
        }
    }
}
