package su.mandora.tarasande.clientmenu

import su.mandora.tarasande.screen.ScreenBetterParentPopupSettings
import su.mandora.tarasande.screen.list.protocolhack.ScreenBetterProtocolHack
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.clientmenu.ElementMenuScreen
import su.mandora.tarasande.base.clientmenu.ElementMenuToggle
import su.mandora.tarasande.module.exploit.ModuleBungeeHack
import su.mandora.tarasande.screen.list.accountmanager.ScreenBetterAccountManager
import su.mandora.tarasande.screen.list.proxy.ScreenBetterProxy

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
