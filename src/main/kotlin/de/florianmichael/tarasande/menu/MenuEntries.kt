package de.florianmichael.tarasande.menu

import de.florianmichael.tarasande.base.menu.ElementMenuScreen
import de.florianmichael.tarasande.screen.ScreenBetterProtocolHack
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.TitleScreen
import su.mandora.tarasande.screen.accountmanager.ScreenBetterAccountManager
import su.mandora.tarasande.screen.proxy.ScreenBetterProxy

class ElementMenuScreenAccountManager : ElementMenuScreen("Account Manager") {

    val screenBetterAccountManager = ScreenBetterAccountManager()

    override fun getScreen(): Screen {
        this.screenBetterAccountManager.prevScreen = MinecraftClient.getInstance().currentScreen
        return this.screenBetterAccountManager
    }
}

class ElementMenuScreenProxySystem : ElementMenuScreen("Proxy System") {

    override fun getScreen(): Screen {
        return ScreenBetterProxy(MinecraftClient.getInstance().currentScreen)
    }
}

class ElementMenuScreenProtocolHack : ElementMenuScreen("Protocol Hack") {

    override fun getScreen(): Screen {
        return ScreenBetterProtocolHack(MinecraftClient.getInstance().currentScreen!!)
    }
}
