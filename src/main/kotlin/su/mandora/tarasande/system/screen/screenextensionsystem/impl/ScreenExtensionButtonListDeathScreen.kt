package su.mandora.tarasande.system.screen.screenextensionsystem.impl

import net.minecraft.client.gui.screen.DeathScreen
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList

class ScreenExtensionButtonListDeathScreen : ScreenExtensionButtonList<DeathScreen>(DeathScreen::class.java) {

    init {
        add(Button("Fake respawn") {
            mc.player?.init()
            mc.setScreen(null)
        })
        add(Button("Force respawn") {
            mc.player?.requestRespawn()
        })
    }
}
