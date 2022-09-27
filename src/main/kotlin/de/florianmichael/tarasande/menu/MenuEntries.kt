package de.florianmichael.tarasande.menu

import de.florianmichael.tarasande.base.menu.ElementMenuScreen
import net.minecraft.client.gui.screen.Screen
import su.mandora.tarasande.screen.accountmanager.ScreenBetterAccountManager

class ElementMenuScreenAccountManager : ElementMenuScreen("Account Manager") {

    companion object {
        val screenBetterAccountManager = ScreenBetterAccountManager()
    }

    override fun getScreen(): Screen {
        return screenBetterAccountManager
    }
}
