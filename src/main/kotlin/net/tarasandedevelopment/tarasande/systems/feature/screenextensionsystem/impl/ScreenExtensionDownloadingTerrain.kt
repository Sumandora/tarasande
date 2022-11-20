package net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.DownloadingTerrainScreen
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.ScreenExtension
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.util.render.helper.Alignment

class ScreenExtensionDownloadingTerrainCancel : ScreenExtension<DownloadingTerrainScreen>("Cancel", DownloadingTerrainScreen::class.java, alignment = Alignment.MIDDLE) {

    override fun onClick(current: DownloadingTerrainScreen) {
        MinecraftClient.getInstance().setScreen(null)
    }
}

class ScreenExtensionDownloadingTerrainCancelAndDisconnect : ScreenExtension<DownloadingTerrainScreen>("Cancel and disconnect", DownloadingTerrainScreen::class.java, alignment = Alignment.MIDDLE) {

    override fun onClick(current: DownloadingTerrainScreen) {
        PlayerUtil.disconnect()
    }
}
