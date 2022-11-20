package net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.DownloadingTerrainScreen
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.ScreenExtensionButton
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.util.render.helper.Alignment

class ScreenExtensionDownloadingTerrainCancel : ScreenExtensionButton<DownloadingTerrainScreen>("Cancel", DownloadingTerrainScreen::class.java, alignment = Alignment.MIDDLE) {

    override fun onClick(current: DownloadingTerrainScreen) {
        MinecraftClient.getInstance().setScreen(null)
    }
}

class ScreenExtensionDownloadingTerrainCancelAndDisconnect : ScreenExtensionButton<DownloadingTerrainScreen>("Cancel and disconnect", DownloadingTerrainScreen::class.java, alignment = Alignment.MIDDLE) {

    override fun onClick(current: DownloadingTerrainScreen) {
        PlayerUtil.disconnect()
    }
}
