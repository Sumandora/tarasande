package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.directconnect

import net.minecraft.client.gui.screen.DirectConnectScreen
import net.minecraft.client.network.AllowedAddressResolver
import net.minecraft.client.network.ServerAddress
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList
import net.tarasandedevelopment.tarasande.util.extension.mc

class ScreenExtensionButtonListDirectConnect : ScreenExtensionButtonList<DirectConnectScreen>(DirectConnectScreen::class.java) {

    init {
        add("Resolve SRV") {
            val current = mc.currentScreen as DirectConnectScreen

            AllowedAddressResolver.DEFAULT.resolve(ServerAddress.parse(current.addressField.text)).map { it.inetSocketAddress }.apply {
                if (isPresent) mc.keyboard.clipboard = get().hostName + ":" + get().port
            }
        }
    }
}
