package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.SleepingChatScreen
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList

class ScreenExtensionButtonListSleepingChatScreen : ScreenExtensionButtonList<SleepingChatScreen>(SleepingChatScreen::class.java) {

    init {
        add("Client wakeup") {
            MinecraftClient.getInstance().apply {
                player?.wakeUp()
                setScreen(null)
            }
        }
    }
}
