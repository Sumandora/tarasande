package net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.serverpinger

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.DirectConnectScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.util.Formatting
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.ScreenExtensionCustom
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.serverpinger.graph.GraphPing
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.serverpinger.graph.GraphPlayers
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.serverpinger.panel.PanelServerInformationPinging
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.panel.PanelGraph
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.ClickableWidgetPanel
import su.mandora.event.EventDispatcher

class ScreenExtensionCustomDirectConnect : ScreenExtensionCustom<DirectConnectScreen>("Server Pinger", DirectConnectScreen::class.java) {

    private val graphPlayers = PanelGraph(GraphPlayers)
    private val graphPing = PanelGraph(GraphPing)

    private var oldAddress = ""
    private val serverPingerWidget = PanelServerInformationPinging {
        if (it.address != oldAddress) {
            graphPlayers.graph.clear()
            graphPing.graph.clear()
        }
        oldAddress = it.address
        Formatting.strip(it.playerCountLabel.string)?.apply {
            if (this.contains("/")) {
                graphPlayers.graph.add(this.split("/")[0].toIntOrNull() ?: return@apply)
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

        GraphPlayers.clear()
        GraphPing.clear()

        return listOf(
            ClickableWidgetPanel(serverPingerWidget),
            ClickableWidgetPanel(graphPlayers),
            ClickableWidgetPanel(graphPing)
        )
    }

    override fun isVisible() = !MinecraftClient.getInstance().isInSingleplayer
}
