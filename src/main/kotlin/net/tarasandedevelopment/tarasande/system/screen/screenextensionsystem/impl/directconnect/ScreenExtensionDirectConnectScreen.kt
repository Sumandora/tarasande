package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.directconnect

import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.DirectConnectScreen
import net.tarasandedevelopment.tarasande.event.EventKey
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtension
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.directconnect.serverpinger.ServerPingerBase
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.directconnect.serverpinger.panel.PanelServerInformation
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.directconnect.serverpinger.panel.copy
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.directconnect.serverpinger.panel.emptyServer
import org.lwjgl.glfw.GLFW
import su.mandora.event.EventDispatcher

class ScreenExtensionDirectConnectScreen : ScreenExtension<DirectConnectScreen>(DirectConnectScreen::class.java) {

    private val serverPingerBase = ServerPingerBase(this) {
        mc.currentScreen.apply {
            if (this is DirectConnectScreen && addressField != null) {
                return@ServerPingerBase addressField.text
            }
        }
        return@ServerPingerBase ""
    }

    init {
        EventDispatcher.apply {
            add(EventKey::class.java) {
                if (it.action == GLFW.GLFW_PRESS || it.action == GLFW.GLFW_REPEAT) {
                    serverPingerBase.apply {
                        pingTask.time = pingTask.time.coerceAtLeast((System.currentTimeMillis() - (delay.value - 500)).toLong())
                    }
                }
            }
        }
    }

    override fun createElements(screen: DirectConnectScreen): MutableList<Element> {
        return mutableListOf(
            serverPingerBase.widget().apply {
                (panel as PanelServerInformation).server = emptyServer.copy()
                x = (screen.width / 2 - panel.panelWidth / 2).toInt()
                y = 30
            }
        )
    }
}
