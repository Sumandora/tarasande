package net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.SleepingChatScreen
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.ScreenExtensionButton

class ScreenExtensionButtonSleepingChat : ScreenExtensionButton<SleepingChatScreen>("Client wakeup", SleepingChatScreen::class.java) {

    override fun onClick(current: SleepingChatScreen) {
        MinecraftClient.getInstance().player?.wakeUp()
        MinecraftClient.getInstance().setScreen(null)
    }
}