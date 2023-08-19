package su.mandora.tarasande.system.screen.screenextensionsystem.impl

import net.minecraft.client.gui.screen.DownloadingTerrainScreen
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList
import su.mandora.tarasande.util.player.PlayerUtil

class ScreenExtensionButtonListDownloadingTerrainScreen : ScreenExtensionButtonList<DownloadingTerrainScreen>(DownloadingTerrainScreen::class.java) {

    init {
        add(Button("Cancel", position = Position.MIDDLE) {
            mc.setScreen(null)
        })
        add(Button("Disconnect", position = Position.MIDDLE) {
            PlayerUtil.disconnect()
        })
    }
}
