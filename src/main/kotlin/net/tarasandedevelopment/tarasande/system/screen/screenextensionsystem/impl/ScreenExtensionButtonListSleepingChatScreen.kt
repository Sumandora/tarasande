package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl

import net.minecraft.client.gui.screen.SleepingChatScreen
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList
import net.tarasandedevelopment.tarasande.util.extension.mc

class ScreenExtensionButtonListSleepingChatScreen : ScreenExtensionButtonList<SleepingChatScreen>(SleepingChatScreen::class.java) {

    init {
        add("Client wakeup") {
            mc.player?.wakeUp()
            mc.setScreen(null)
        }
    }
}
