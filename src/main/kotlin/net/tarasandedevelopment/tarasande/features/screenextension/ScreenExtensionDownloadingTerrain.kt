package net.tarasandedevelopment.tarasande.features.screenextension

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.DownloadingTerrainScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.minecraft.client.realms.gui.screen.RealmsMainScreen
import net.tarasandedevelopment.tarasande.base.features.screenextension.ScreenExtension
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.Alignment

class ScreenExtensionDownloadingTerrainCancel : ScreenExtension("Cancel", DownloadingTerrainScreen::class.java, alignment = Alignment.MIDDLE) {

    override fun onClick(current: Screen) {
        MinecraftClient.getInstance().setScreen(null)
    }
}

class ScreenExtensionDownloadingTerrainCancelAndDisconnect : ScreenExtension("Cancel and disconnect", DownloadingTerrainScreen::class.java, alignment = Alignment.MIDDLE) {

    override fun onClick(current: Screen) {
        MinecraftClient.getInstance().world?.disconnect()
        MinecraftClient.getInstance().disconnect()

        val title = TitleScreen()

        if (MinecraftClient.getInstance().isInSingleplayer) {
            MinecraftClient.getInstance().setScreen(title)
        } else if (MinecraftClient.getInstance().isConnectedToRealms) {
            MinecraftClient.getInstance().setScreen(RealmsMainScreen(title))
        } else {
            MinecraftClient.getInstance().setScreen(MultiplayerScreen(title))
        }
    }
}
