package de.florianmichael.tarasande.menu

import de.florianmichael.tarasande.base.menu.ElementMenuScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.TitleScreen
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.screen.accountmanager.ScreenBetterAccountManager

class ElementMenuScreenAccountManager : ElementMenuScreen("Account Manager") {

    val screenBetterAccountManager = ScreenBetterAccountManager()

    override fun getScreen(): Screen {
        return this.screenBetterAccountManager
    }
}

class ElementMenuScreenProxySystem : ElementMenuScreen("Proxy System") {

    override fun getScreen(): Screen {
        return TitleScreen() // Not implemented yet
    }
}

class ElementMenuScreenProtocolHack : ElementMenuScreen("Protocol Hack") {

    override fun getScreen(): Screen {
        return TitleScreen() // Not implemented yet
    }
}
