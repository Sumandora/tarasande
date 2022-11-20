package net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.DeathScreen
import net.minecraft.client.gui.screen.SleepingChatScreen
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.ScreenExtension

class ScreenExtensionMinecraftMenusSleepingChat : ScreenExtension<SleepingChatScreen>("Client wakeup", SleepingChatScreen::class.java) {

    override fun onClick(current: SleepingChatScreen) {
        MinecraftClient.getInstance().player?.wakeUp()
        MinecraftClient.getInstance().setScreen(null)
    }
}

class ScreenExtensionMinecraftMenusDeath : ScreenExtension<DeathScreen>("Force respawn", DeathScreen::class.java) {

    override fun onClick(current: DeathScreen) {
        MinecraftClient.getInstance().player?.requestRespawn()
        MinecraftClient.getInstance().setScreen(null)
    }
}
