package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl

import net.minecraft.client.gui.screen.DeathScreen
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList
import net.tarasandedevelopment.tarasande.util.extension.mc

class ScreenExtensionButtonListDeathScreen : ScreenExtensionButtonList<DeathScreen>(DeathScreen::class.java) {

    init {
        add("Fake respawn") {
            mc.player?.init()
            mc.setScreen(null)
        }
        add("Force respawn") {
            mc.player?.requestRespawn()
        }
    }
}
