package su.mandora.tarasande.feature.screens.accountmanager.screenextension

import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import su.mandora.tarasande.feature.screens.Screens
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList

class ScreenExtensionButtonListMultiplayerScreen : ScreenExtensionButtonList<MultiplayerScreen>(MultiplayerScreen::class.java) {

    init {
        add(Button("Account manager") {
            mc.setScreen(Screens.screenBetterSlotListAccountManager.apply { prevScreen = mc.currentScreen })
        })
    }
}
