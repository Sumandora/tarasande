package net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.DirectConnectScreen
import net.minecraft.client.gui.screen.GameMenuScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget
import net.minecraft.client.network.MultiplayerServerListPinger
import net.minecraft.client.network.ServerInfo
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.screen.ScreenTexts
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.screen.widget.serverpinger.WidgetServerInformation
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.ScreenExtensionCustom
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.api.ClickableWidgetPanel
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import net.tarasandedevelopment.tarasande.util.threading.ThreadRunnableExposed
import su.mandora.event.EventDispatcher
import java.net.InetSocketAddress
import java.net.UnknownHostException

fun getAddress(): String {
    MinecraftClient.getInstance().currentScreen.apply {
        if (this is DirectConnectScreen && addressField != null) {
            return addressField.text
        }
    }
    if (!MinecraftClient.getInstance().isInSingleplayer) {
        MinecraftClient.getInstance().networkHandler?.connection?.address?.also {
            return (it as InetSocketAddress).hostString + ":" + it.port
        }
    }
    return ""
}

class WidgetServerInformationPinging : WidgetServerInformation() {

    private val pingDelay = ValueNumber(this, "Ping delay", 100.0, 5000.0, 10000.0, 100.0)

    private val timer = TimeUtil()
    private var pinger: MultiplayerServerListPinger? = null

    override fun init() {
        if (server == null) {
            getAddress().apply {
                server = ServerInfo(this, this, false)
                server?.apply {
                    ping = -2L
                    label = ScreenTexts.EMPTY
                    playerCountLabel = ScreenTexts.EMPTY
                }
            }
            timer.time = pingDelay.value.toLong()
        }
    }

    fun ping() {
        if (timer.hasReached(pingDelay.value.toLong())) {
            getAddress().apply {
                server?.name = this
                server?.address = this
            }
            try {
                pinger?.apply {
                    cancel()
                }
                val pinger = MultiplayerServerListPinger()
                ThreadRunnableExposed {
                    pinger.add(server) {
                        // this doesn't work right since Minecraft is to lazy to implement a good saving function
                    }
                }.start()
                this.pinger = pinger
            } catch (e: UnknownHostException) {
                server?.ping = -1L
                server?.label = MultiplayerServerListWidget.CANNOT_RESOLVE_TEXT
            } catch (e2: Exception) {
                server?.ping = -1L
                server?.label = MultiplayerServerListWidget.CANNOT_CONNECT_TEXT
            }
            recreateIcon(getAddress())
            timer.reset()
        }
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        ping()
        super.render(matrices, mouseX, mouseY, delta)


        (((pingDelay.value + 1000) - (System.currentTimeMillis() - timer.time)) / 1000).toInt().toString().also {
            FontWrapper.textShadow(matrices, it, (x + panelWidth - FontWrapper.getWidth(it)).toFloat(), (y).toFloat())
        }
    }
}

class ScreenExtensionServerPingerDirectConnect : ScreenExtensionCustom<DirectConnectScreen>("Server Pinger", DirectConnectScreen::class.java) {

    private val serverPingerWidget = WidgetServerInformationPinging()

    override fun createElements(screen: Screen): List<Element> {
        serverPingerWidget.x = MinecraftClient.getInstance().currentScreen!!.width / 2 - serverPingerWidget.panelWidth / 2
        serverPingerWidget.y = 30.0

        return listOf(
            ClickableWidgetPanel(serverPingerWidget)
        )
    }
}

class ScreenExtensionServerPingerGameMenu : ScreenExtensionCustom<Screen>("Server Pinger", GameMenuScreen::class.java) {

    private val serverPingerWidget = WidgetServerInformationPinging()
    private val pingWhenIngame = ValueBoolean(serverPingerWidget, "Ping when in game", true)

    init {
        EventDispatcher.add(EventTick::class.java) {
            if (pingWhenIngame.value && MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
                serverPingerWidget.ping()
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
