package net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.DownloadingTerrainScreen
import net.minecraft.client.gui.screen.Screen
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.ScreenExtension
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.util.render.helper.Alignment

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
