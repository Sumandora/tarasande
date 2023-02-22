package de.florianmichael.tarasande_serverpinger.screenextension.directconnect

import de.florianmichael.tarasande_serverpinger.base.*
import de.florianmichael.tarasande_serverpinger.base.panel.PanelServerInformation
import de.florianmichael.tarasande_serverpinger.base.panel.copy
import de.florianmichael.tarasande_serverpinger.base.panel.emptyServer
import de.florianmichael.tarasande_serverpinger.injection.accessor.IClickableWidget
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.DirectConnectScreen
import net.tarasandedevelopment.tarasande.event.EventKey
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.panel.PanelGraph
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.ClickableWidgetPanel
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtension
import org.lwjgl.glfw.GLFW
import su.mandora.event.EventDispatcher

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
            panelElementsIanaFunction.update(it.address)
        }
        lastAddress = it.address
        update(it)
    }

    private val panelElementsIanaFunction = PanelElementsIanaFunction()

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

    override fun createElements(screen: DirectConnectScreen): MutableList<Element> {
        return mutableListOf(serverPingerBase.widget().apply {
            (panel as PanelServerInformation).server = emptyServer.copy()
            (this as IClickableWidget).tarasande_init((screen.width / 2 - panel.panelWidth / 2).toInt(), 30)
        }, ClickableWidgetPanel(graphPlayers.apply {
            x = screen.width - panelWidth - 5.0
            y = screen.height - panelHeight - 5.0
        }), ClickableWidgetPanel(graphPing.apply {
            x = screen.width - panelWidth - 5.0
            y = graphPlayers.y - panelHeight - 5.0
        }), ClickableWidgetPanel(panelElementsIanaFunction.apply {
            x = 5.0
            y = screen.height - panelHeight - 5.0
        }))
    }
}
