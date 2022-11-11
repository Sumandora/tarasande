package net.tarasandedevelopment.tarasande.features.screenextension

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.DownloadingTerrainScreen
import net.minecraft.client.gui.screen.Screen
import net.tarasandedevelopment.tarasande.base.features.screenextension.ScreenExtension
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.Alignment
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil

class ScreenExtensionDownloadingTerrainCancel : ScreenExtension("Cancel", DownloadingTerrainScreen::class.java, alignment = Alignment.MIDDLE) {

    override fun onClick(current: Screen) {
        MinecraftClient.getInstance().setScreen(null)
    }
}

class ScreenExtensionDownloadingTerrainCancelAndDisconnect : ScreenExtension("Cancel and disconnect", DownloadingTerrainScreen::class.java, alignment = Alignment.MIDDLE) {

    override fun onClick(current: Screen) {
        PlayerUtil.disconnect()
    }
}
