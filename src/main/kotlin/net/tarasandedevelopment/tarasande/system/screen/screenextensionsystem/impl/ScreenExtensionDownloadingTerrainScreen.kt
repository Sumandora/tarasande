package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl

import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.DownloadingTerrainScreen
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.impl.button.PanelButton
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtension
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil

class ScreenExtensionDownloadingTerrainScreen : ScreenExtension<DownloadingTerrainScreen>(DownloadingTerrainScreen::class.java) {

    override fun createElements(screen: DownloadingTerrainScreen): MutableList<Element> {
        return mutableListOf(
            PanelButton.createButton(screen.width / 2 - 48, 3, 98, 25, "Cancel") {
                mc.setScreen(null)
            },
            PanelButton.createButton(screen.width / 2 - 48, 3 + 30, 98, 25, "Cancel and disconnect") {
                PlayerUtil.disconnect()
            }
        )
    }
}
