package net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.DirectConnectScreen
import net.minecraft.client.gui.screen.GameMenuScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.ServerInfo
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.screen.ScreenTexts
import net.tarasandedevelopment.tarasande.event.EventConnectServer
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.screen.widget.serverpinger.PanelServerInformation
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.ScreenExtensionCustom
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.api.ClickableWidgetPanel
import net.tarasandedevelopment.tarasande.util.connection.AddressSaver
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import su.mandora.event.EventDispatcher
import java.net.InetSocketAddress

class PanelServerInformationPinging : PanelServerInformation() {

    private val pingDelay = ValueNumber(this, "Ping delay", 100.0, 5000.0, 10000.0, 100.0)

    private val timer = TimeUtil()

    override fun updateServerInfo() = AddressSaver.getAddress().let {
        ServerInfo(it, it, false).apply {
            ping = 0L
            label = ScreenTexts.EMPTY
            playerCountLabel = ScreenTexts.EMPTY
            online = false
        }
    }

    override fun init() {
    }

    fun ping(force: Boolean = false) {
        if (force || timer.hasReached(pingDelay.value.toLong())) {
            AddressSaver.getAddress().apply {
                server.name = this
                server.address = this
            }
            server.online = false
            serverEntry = createEntry()
            timer.reset()
        }
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        ping()
        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun renderTitleBar(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.renderTitleBar(matrices, mouseX, mouseY, delta)

        (((pingDelay.value + 1000) - (System.currentTimeMillis() - timer.time)) / 1000).toInt().toString().also {
            FontWrapper.textShadow(matrices, it, (x + panelWidth - FontWrapper.getWidth(it)).toFloat(), (y).toFloat())
        }
    }
}

class ScreenExtensionServerPingerDirectConnect : ScreenExtensionCustom<DirectConnectScreen>("Server Pinger", DirectConnectScreen::class.java) {

    private val serverPingerWidget = PanelServerInformationPinging()


    var lastText: String? = null

    init {
        EventDispatcher.add(EventTick::class.java) {
            val screen = MinecraftClient.getInstance().currentScreen
            if(screen is DirectConnectScreen) {
                screen.addressField.text.apply {
                    if(lastText != this) {
                        serverPingerWidget.server = serverPingerWidget.updateServerInfo()
                        serverPingerWidget.ping(true)
                    }
                    lastText = this
                }
            }
        }
    }

    override fun createElements(screen: Screen): List<Element> {
        serverPingerWidget.x = MinecraftClient.getInstance().currentScreen!!.width / 2 - serverPingerWidget.panelWidth / 2
        serverPingerWidget.y = 30.0

        return listOf(
            ClickableWidgetPanel(serverPingerWidget)
        )
    }
}

class ScreenExtensionServerPingerGameMenu : ScreenExtensionCustom<Screen>("Server Pinger", GameMenuScreen::class.java) {

    private val serverPingerWidget = PanelServerInformationPinging()
    private val pingWhenInGame: ValueBoolean = ValueBoolean(serverPingerWidget, "Ping when in game", true)

    init {
        EventDispatcher.add(EventTick::class.java) { event ->
            if (event.state == EventTick.State.PRE) {
                if (pingWhenInGame.value && MinecraftClient.getInstance().currentScreen !is GameMenuScreen && MinecraftClient.getInstance().player != null) {
                    serverPingerWidget.ping(false)
                }
            }
        }
    }

    override fun createElements(screen: Screen): List<Element> {
        serverPingerWidget.x = MinecraftClient.getInstance().currentScreen!!.width / 2 - serverPingerWidget.panelWidth / 2
        serverPingerWidget.y = 50.0

        return listOf(
            ClickableWidgetPanel(serverPingerWidget)
        )
    }
}
