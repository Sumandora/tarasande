package su.mandora.tarasande.system.screen.screenextensionsystem.impl

import net.minecraft.client.gui.screen.SleepingChatScreen
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList

class ScreenExtensionButtonListSleepingChatScreen : ScreenExtensionButtonList<SleepingChatScreen>(SleepingChatScreen::class.java) {

    init {
        add(Button("Client wakeup") {
            mc.player?.wakeUp()
            mc.setScreen(null)
        })
    }
}
