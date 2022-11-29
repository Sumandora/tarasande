package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.DeathScreen
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList

class ScreenExtensionButtonListDeathScreen : ScreenExtensionButtonList<DeathScreen>(DeathScreen::class.java) {

    init {
        add("Fake respawn") {
            MinecraftClient.getInstance().player?.apply { init() }
            MinecraftClient.getInstance().setScreen(null)
        }
        add("Force respawn") {
            MinecraftClient.getInstance().player?.requestRespawn()
        }
    }
}
