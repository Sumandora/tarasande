package net.tarasandedevelopment.tarasande.screen.cheatmenu.clientmenu

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.util.Util
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.ElementMenu
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.ElementMenuScreen
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.ElementMenuTitle
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.ElementMenuToggle
import net.tarasandedevelopment.tarasande.module.exploit.ModuleBungeeHack
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterParentPopupSettings
import net.tarasandedevelopment.tarasande.screen.clientmenu.accountmanager.ScreenBetterAccountManager
import net.tarasandedevelopment.tarasande.screen.clientmenu.protocolhack.ScreenBetterProtocolHack
import net.tarasandedevelopment.tarasande.screen.clientmenu.proxy.ScreenBetterProxy
import org.lwjgl.glfw.GLFW
import org.spongepowered.include.com.google.common.io.Files
import java.io.File

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

    var screenBetterProtocolHack = ScreenBetterProtocolHack()

    override fun getScreen(): Screen {
        screenBetterProtocolHack.prevScreen = MinecraftClient.getInstance().currentScreen
        return this.screenBetterProtocolHack
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

class ElementMenuFritzBoxReconnect : ElementMenu("Fritz!Box Reconnect") {
    private val scriptName = "ip_changer_fritzbox.vbs"
    private val script = File(TarasandeMain.get().rootDirectory, scriptName)

    init {
        if (visible())
            if (!script.exists())
                Files.write(TarasandeMain::class.java.getResourceAsStream(scriptName)?.readAllBytes() ?: error("$scriptName not found"), script)
    }

    override fun onClick(mouseButton: Int) {
        val builder = ProcessBuilder("wscript", this.script.absolutePath)

        builder.directory(TarasandeMain.get().rootDirectory)
        builder.start()
    }

    open class SubTitle(private val parent: ElementMenuFritzBoxReconnect) : ElementMenuTitle("Special") {
        override fun visible() = parent.visible()
    }

    override fun visible() = Util.getOperatingSystem() == Util.OperatingSystem.WINDOWS
}
