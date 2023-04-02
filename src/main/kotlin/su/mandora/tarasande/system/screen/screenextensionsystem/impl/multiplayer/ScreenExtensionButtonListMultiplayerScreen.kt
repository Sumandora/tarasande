package su.mandora.tarasande.system.screen.screenextensionsystem.impl.multiplayer

import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList
import su.mandora.tarasande.system.screen.screenextensionsystem.impl.multiplayer.accountmanager.ScreenBetterSlotListAccountManager

class ScreenExtensionButtonListMultiplayerScreen : ScreenExtensionButtonList<MultiplayerScreen>(MultiplayerScreen::class.java) {

    val screenBetterSlotListAccountManager = ScreenBetterSlotListAccountManager()

    init {
        add("Account manager") {
            mc.setScreen(screenBetterSlotListAccountManager.apply { prevScreen = mc.currentScreen })
        }
    }
}
