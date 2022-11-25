package net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.downloadingterrain

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.DownloadingTerrainScreen
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.ScreenExtensionButton
import net.tarasandedevelopment.tarasande.util.render.helper.Alignment

class ScreenExtensionButtonCancel : ScreenExtensionButton<DownloadingTerrainScreen>("Cancel", DownloadingTerrainScreen::class.java, alignment = Alignment.MIDDLE) {

    override fun onClick(current: DownloadingTerrainScreen) {
        MinecraftClient.getInstance().setScreen(null)
    }
}