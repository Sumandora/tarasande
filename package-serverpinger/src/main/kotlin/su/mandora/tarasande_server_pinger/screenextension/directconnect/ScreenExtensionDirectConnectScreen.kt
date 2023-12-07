package su.mandora.tarasande_server_pinger.screenextension.directconnect

import net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventKey
import su.mandora.tarasande.injection.accessor.IScreen
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.graphsystem.panel.PanelGraph
import su.mandora.tarasande.system.screen.panelsystem.api.ClickableWidgetPanel
import su.mandora.tarasande.system.screen.screenextensionsystem.ScreenExtension
import su.mandora.tarasande_server_pinger.base.GraphPing
import su.mandora.tarasande_server_pinger.base.GraphPlayers
import su.mandora.tarasande_server_pinger.base.ServerPingerBase
import su.mandora.tarasande_server_pinger.base.panel.PanelServerInformation
import su.mandora.tarasande_server_pinger.base.panel.copy
import su.mandora.tarasande_server_pinger.base.panel.emptyServer
import su.mandora.tarasande_server_pinger.base.update
import su.mandora.tarasande_server_pinger.injection.accessor.IClickableWidget

class ScreenExtensionDirectConnectScreen : ScreenExtension<DirectConnectScreen>(DirectConnectScreen::class.java) {

    private var lastAddress = ""

    private val serverPingerBase = ServerPingerBase(this, {
        mc.currentScreen.apply {
            if (this is DirectConnectScreen && addressField != null) return@ServerPingerBase addressField.text
        }
        return@ServerPingerBase ""
    }) {
        if (lastAddress != it.address) {
            GraphPlayers.clear()
            GraphPing.clear()
        }
        lastAddress = it.address
        update(it)
    }

    private val graphPlayers = PanelGraph(GraphPlayers)
    private val graphPing = PanelGraph(GraphPing)

    init {
        EventDispatcher.apply {
            add(EventKey::class.java) {
                if (it.action == GLFW.GLFW_PRESS || it.action == GLFW.GLFW_REPEAT) {
                    serverPingerBase.apply {
                        pingTask.time =
                            pingTask.time.coerceAtLeast((System.currentTimeMillis() - (delay.value - 500)).toLong())
                    }
                }
            }
        }
    }

    override fun createElements(screen: DirectConnectScreen) {
        (screen as IScreen).tarasande_addDrawableChild(serverPingerBase.widget().apply {
            (panel as PanelServerInformation).server = emptyServer.copy()
            (this as IClickableWidget).tarasande_init((screen.width / 2 - panel.panelWidth / 2).toInt(), 30)
        })
        (screen as IScreen).tarasande_addDrawableChild(ClickableWidgetPanel(graphPlayers.apply {
            x = screen.width - panelWidth - 5.0
            y = screen.height - panelHeight - 5.0
        }))
        (screen as IScreen).tarasande_addDrawableChild(ClickableWidgetPanel(graphPing.apply {
            x = screen.width - panelWidth - 5.0
            y = graphPlayers.y - panelHeight - 5.0
        }))
    }
}
