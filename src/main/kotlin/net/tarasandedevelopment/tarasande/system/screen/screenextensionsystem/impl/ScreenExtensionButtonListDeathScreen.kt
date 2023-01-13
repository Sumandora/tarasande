package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl

import net.minecraft.client.gui.screen.DeathScreen
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList

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
