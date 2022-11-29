package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.GameMenuScreen
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtension
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.serverpinger.ServerPingerBase

class ScreenExtensionGameMenuScreen : ScreenExtension<GameMenuScreen>(GameMenuScreen::class.java) {

    private val serverPingerBase = ServerPingerBase(this) {
        return@ServerPingerBase MinecraftClient.getInstance().currentServerEntry?.address?: ""
    }

    override fun createElements(screen: GameMenuScreen): MutableList<Element> {
        return mutableListOf(
            serverPingerBase.widget().apply {
                x = (screen.width / 2 - panel.panelWidth / 2).toInt()
                y = 50
            }
        )
    }
}
