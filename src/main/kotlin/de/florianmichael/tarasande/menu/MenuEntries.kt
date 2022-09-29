package de.florianmichael.tarasande.menu

import de.florianmichael.tarasande.base.menu.ElementMenu
import de.florianmichael.tarasande.base.menu.ElementMenuScreen
import de.florianmichael.tarasande.base.menu.ElementMenuToggle
import de.florianmichael.tarasande.module.exploit.ModuleBungeeHack
import de.florianmichael.tarasande.screen.ScreenBetterParentPopupSettings
import de.florianmichael.tarasande.screen.ScreenBetterProtocolHack
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.TitleScreen
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.TarasandeMain
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

class ElementMenuToggleBungeeHack : ElementMenuToggle("Bungee Hack") {

    override fun onToggle(state: Boolean) {
        TarasandeMain.get().managerModule.get(ModuleBungeeHack::class.java).enabled = state
    }

    override fun otherMouseHandling(button: Int) {
        super.otherMouseHandling(button)

        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            MinecraftClient.getInstance().setScreen(ScreenBetterParentPopupSettings(MinecraftClient.getInstance().currentScreen!!, this.name, TarasandeMain.get().managerModule.get(ModuleBungeeHack::class.java)))
        }
    }
}
