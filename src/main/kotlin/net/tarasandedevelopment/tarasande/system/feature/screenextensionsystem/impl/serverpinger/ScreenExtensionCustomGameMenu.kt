package net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.serverpinger

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.GameMenuScreen
import net.minecraft.client.gui.screen.Screen
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.ScreenExtensionCustom
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.serverpinger.panel.PanelServerInformationPinging
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.ClickableWidgetPanel
import su.mandora.event.EventDispatcher

class ScreenExtensionCustomGameMenu : ScreenExtensionCustom<Screen>("Server Pinger", GameMenuScreen::class.java) {

    private val serverPingerWidget = PanelServerInformationPinging()
    private val pingWhenInGame: ValueBoolean = ValueBoolean(serverPingerWidget, "Ping when in game", true)

    init {
        EventDispatcher.add(EventTick::class.java) { event ->
            if (event.state == EventTick.State.PRE) {
                if (pingWhenInGame.value && MinecraftClient.getInstance().currentScreen !is GameMenuScreen && MinecraftClient.getInstance().player != null) {
                    serverPingerWidget.ping(false)
                }
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