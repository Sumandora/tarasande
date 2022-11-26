package net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.serverpinger

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.DirectConnectScreen
import net.minecraft.client.gui.screen.Screen
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.ScreenExtensionCustom
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.serverpinger.panel.PanelServerInformationPinging
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.ClickableWidgetPanel
import su.mandora.event.EventDispatcher

class ScreenExtensionCustomDirectConnect : ScreenExtensionCustom<DirectConnectScreen>("Server Pinger", DirectConnectScreen::class.java) {

    private val serverPingerWidget = PanelServerInformationPinging()

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

        return listOf(
            ClickableWidgetPanel(serverPingerWidget)
        )
    }

    override fun isVisible() = !MinecraftClient.getInstance().isInSingleplayer
}
