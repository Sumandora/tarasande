package net.tarasandedevelopment.tarasande.screen.clientmenu

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket
import net.minecraft.util.Util
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.ElementMenu
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.ElementMenuScreen
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.ElementMenuTitle
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.ElementMenuToggle
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.mixin.accessor.IHandshakeC2SPacket
import net.tarasandedevelopment.tarasande.screen.clientmenu.accountmanager.ScreenBetterAccountManager
import net.tarasandedevelopment.tarasande.screen.clientmenu.protocol.ScreenBetterProtocolHack
import net.tarasandedevelopment.tarasande.screen.clientmenu.proxy.ScreenBetterProxy
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueText
import org.spongepowered.include.com.google.common.io.Files
import java.io.File
import java.util.*
import java.util.function.Consumer

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

    private val endIP = ValueText(this, "End IP", "127.0.0.1")
    private val customUUID = ValueBoolean(this, "Custom UUID", false)
    private val uuid = object : ValueText(this, "UUID", UUID.randomUUID().toString()) {

        override fun isEnabled() = customUUID.value
    }

    private val zero = "\u0000"
    private fun stripID(input: String) = input.replace("-", "")

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventPacket -> {
                if (event.type != EventPacket.Type.SEND) return@Consumer
                if (event.packet !is HandshakeC2SPacket) return@Consumer

                var uuid = MinecraftClient.getInstance().session.uuid
                if (this.customUUID.value)
                    uuid = this.uuid.value

                (event.packet as IHandshakeC2SPacket).tarasande_extendAddress(this.zero + this.endIP.value + this.zero + this.stripID(uuid))
            }
        }
    }

    override fun onToggle(state: Boolean) {
        if (state)
            TarasandeMain.get().managerEvent.addObject(this)
        else
            TarasandeMain.get().managerEvent.remObject(this)
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
