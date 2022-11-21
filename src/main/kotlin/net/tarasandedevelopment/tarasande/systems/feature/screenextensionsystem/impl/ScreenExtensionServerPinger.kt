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
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import su.mandora.event.EventDispatcher
import java.awt.Color
import java.net.InetSocketAddress
import java.net.UnknownHostException
import java.util.concurrent.CompletableFuture
import kotlin.math.round
import kotlin.math.roundToInt

interface AddressProvider {
    fun get(): String
}

class WidgetServerInformationPinging(private val timer: TimeUtil, private val addressProvider: AddressProvider) : WidgetServerInformation() {

    private val pingDelay = ValueNumber(this, "Ping delay", 100.0, 5000.0, 10000.0, 100.0)

    override fun init() {
        super.init()

        if (server == null) {
            server = ServerInfo(addressProvider.get(), addressProvider.get(), false)
            server!!.ping = -2L
            server!!.label = ScreenTexts.EMPTY
            server!!.playerCountLabel = ScreenTexts.EMPTY
            timer.time = pingDelay.value.toLong()
        }
    }

    fun ping() {
        if (timer.hasReached(pingDelay.value.toLong())) {
            server?.name = addressProvider.get()
            server?.address = addressProvider.get()
            try {
                CompletableFuture.runAsync {
                    MultiplayerServerListPinger().add(server) {
                    }
                }
            } catch (e: UnknownHostException) {
                server?.ping = -1L
                server?.label = MultiplayerServerListWidget.CANNOT_RESOLVE_TEXT
            } catch (e2: Exception) {
                server?.ping = -1L
                server?.label = MultiplayerServerListWidget.CANNOT_CONNECT_TEXT
            }
            recreateIcon(addressProvider.get())
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

class ScreenExtensionServerPingerDirectConnect : ScreenExtensionCustom<Screen>("Server Pinger", DirectConnectScreen::class.java) {

    private val timer = TimeUtil()
    private val serverPingerWidget = WidgetServerInformationPinging(timer, object : AddressProvider {
        override fun get(): String {
            return try {
                (MinecraftClient.getInstance().currentScreen as DirectConnectScreen).addressField.text
            } catch (e: Exception) {
                ""
            }
        }
    })

    override fun createElements(screen: Screen): List<Element> {
        serverPingerWidget.x = MinecraftClient.getInstance().currentScreen!!.width / 2 - serverPingerWidget.panelWidth / 2
        serverPingerWidget.y = 30.0

        return listOf(
            ClickableWidgetPanel(serverPingerWidget)
        )
    }
}

class ScreenExtensionServerPingerGameMenu : ScreenExtensionCustom<Screen>("Server Pinger", GameMenuScreen::class.java) {

    private val timer = TimeUtil()
    private val serverPingerWidget = WidgetServerInformationPinging(timer, object : AddressProvider {
        override fun get(): String {
            if (MinecraftClient.getInstance().networkHandler == null) {
                return ""
            }
            (MinecraftClient.getInstance().networkHandler!!.connection.address as InetSocketAddress).also {
                return it.hostString + ":" + it.port
            }
        }
    })
    private val pingWhenIngame = ValueBoolean(this, "Ping when in game", true)

    init {
        EventDispatcher.add(EventTick::class.java) {
            if (pingWhenIngame.value) {
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
