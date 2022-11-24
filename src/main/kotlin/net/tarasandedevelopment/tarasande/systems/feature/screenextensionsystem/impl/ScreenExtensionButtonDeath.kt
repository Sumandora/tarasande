package net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.DeathScreen
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.ScreenExtensionButton

class ScreenExtensionButtonDeath : ScreenExtensionButton<DeathScreen>("Force respawn", DeathScreen::class.java) {

    override fun onClick(current: DeathScreen) {
        MinecraftClient.getInstance().player?.requestRespawn()
        MinecraftClient.getInstance().setScreen(null)
    }
}
