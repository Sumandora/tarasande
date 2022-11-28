package net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.serverpinger

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.GameMenuScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.util.Formatting
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.ScreenExtensionCustom
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.serverpinger.graph.GraphPing
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.serverpinger.graph.GraphPlayers
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.serverpinger.panel.PanelServerInformationPinging
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.panel.PanelGraph
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.ClickableWidgetPanel
import su.mandora.event.EventDispatcher

class ScreenExtensionCustomGameMenu : ScreenExtensionCustom<Screen>("Server Pinger", GameMenuScreen::class.java) {

    private val graphPlayers = PanelGraph(GraphPlayers)
    private val graphPing = PanelGraph(GraphPing)

    private var oldAddress = ""
    private val serverPingerWidget = PanelServerInformationPinging(this) {
        if (it.address != oldAddress) {
            GraphPlayers.clear()
            GraphPing.clear()
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
    private val pingWhenInGame = ValueBoolean(this, "Ping when in game", true)

    init {
        EventDispatcher.apply {
            add(EventTick::class.java) { event ->
                if (event.state == EventTick.State.PRE) {
                    if (pingWhenInGame.value && MinecraftClient.getInstance().currentScreen !is GameMenuScreen && MinecraftClient.getInstance().player != null) {
                        serverPingerWidget.ping(false)
                    }
                }
            }
            add(EventDisconnect::class.java) {
                if(it.connection == MinecraftClient.getInstance().networkHandler?.connection) {
                    oldAddress = ""
                    GraphPlayers.clear()
                    GraphPing.clear()
                }
            }
        }
    }

    override fun createElements(screen: Screen): List<Element> {
        serverPingerWidget.x = MinecraftClient.getInstance().currentScreen!!.width / 2 - serverPingerWidget.panelWidth / 2
        serverPingerWidget.y = 50.0

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
