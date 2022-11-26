package net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.serverpinger

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.DirectConnectScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.util.Formatting
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.ScreenExtensionCustom
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.serverpinger.panel.PanelServerInformationPinging
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.Graph
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.panel.PanelGraph
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.ClickableWidgetPanel
import su.mandora.event.EventDispatcher

class ScreenExtensionCustomDirectConnect : ScreenExtensionCustom<DirectConnectScreen>("Server Pinger", DirectConnectScreen::class.java) {

    private val graphPlayers = PanelGraph(Graph("Players", 10, true))
    private val graphPing = PanelGraph(Graph("Ping", 10, true))

    private val serverPingerWidget = PanelServerInformationPinging {
        Formatting.strip(it.playerCountLabel.string)?.apply {
            if (this.contains("/")) {
                graphPlayers.graph.add(this.split("/")[0].toInt())
            }
        }
        it.ping.apply {
            graphPing.graph.add(this)
        }
    }

    var lastText: String? = null

    init {
        EventDispatcher.add(EventTick::class.java) {
            val screen = MinecraftClient.getInstance().currentScreen
            if (screen is DirectConnectScreen) {
                screen.addressField.text.apply {
                    if (lastText != this) {
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

        graphPlayers.x = 5.0
        graphPlayers.y = MinecraftClient.getInstance().currentScreen!!.height - graphPlayers.panelHeight - 5.0

        graphPing.x = 5.0
        graphPing.y = graphPlayers.y - graphPing.panelHeight - 5.0

        return listOf(
            ClickableWidgetPanel(serverPingerWidget),
            ClickableWidgetPanel(graphPlayers),
            ClickableWidgetPanel(graphPing)
        )
    }

    override fun isVisible() = !MinecraftClient.getInstance().isInSingleplayer
}
