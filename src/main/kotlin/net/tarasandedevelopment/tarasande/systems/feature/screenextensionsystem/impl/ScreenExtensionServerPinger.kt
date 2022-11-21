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
import net.tarasandedevelopment.tarasande.screen.widget.serverpinger.WidgetServerInformation
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.ScreenExtensionCustom
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.api.ClickableWidgetPanel
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import java.awt.Color
import java.net.InetSocketAddress
import java.net.UnknownHostException
import java.util.concurrent.CompletableFuture
import kotlin.math.round
import kotlin.math.roundToInt

class ScreenExtensionServerPinger : ScreenExtensionCustom<Screen>("Server Pinger", GameMenuScreen::class.java, DirectConnectScreen::class.java) {

    private val timer = TimeUtil()
    private val widget = object : WidgetServerInformation() {
        var currentAddress = ""

        val pingDelay = ValueNumber(this, "Ping delay", 100.0, 5000.0, 10000.0, 100.0)

        init {
            pingDelay.owner = this@ScreenExtensionServerPinger
        }

        override fun init() {
            super.init()

            if (server == null) {
                server = ServerInfo(currentAddress, currentAddress, false)
                server!!.ping = -2L
                server!!.label = ScreenTexts.EMPTY
                server!!.playerCountLabel = ScreenTexts.EMPTY
                timer.reset()
            }
        }

        override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
            if (timer.hasReached(pingDelay.value.toLong())) {
                server?.name = currentAddress
                server?.address = currentAddress
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
                recreateIcon(currentAddress)
                timer.reset()
            }
            super.render(matrices, mouseX, mouseY, delta)

            (((pingDelay.value + 1000) - (System.currentTimeMillis() - timer.time)) / 1000).toInt().toString().also {
                FontWrapper.textShadow(matrices, it, (x + panelWidth - FontWrapper.getWidth(it)).toFloat(), (y).toFloat())
            }
        }
    }

    override fun createElements(screen: Screen): List<Element> {
        val isDirectConnect = screen is DirectConnectScreen
        widget.x = MinecraftClient.getInstance().currentScreen!!.width / 2 - widget.panelWidth / 2
        widget.y = if (isDirectConnect) 30.0 else 50.0

        return listOf(
            object : ClickableWidgetPanel(widget) {
                override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
                    if (isDirectConnect) {
                        widget.currentAddress = (screen as DirectConnectScreen).addressField.text
                    } else {
                        (MinecraftClient.getInstance().networkHandler!!.connection.address as InetSocketAddress).also {
                            widget.currentAddress = it.hostString + ":" + it.port
                        }
                    }
                    super.render(matrices, mouseX, mouseY, delta)
                }
            }
        )
    }
}
