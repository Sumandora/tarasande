package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.accountmanager.ScreenBetterSlotListAccountManager

class ScreenExtensionButtonListMultiplayerScreen : ScreenExtensionButtonList<MultiplayerScreen>(MultiplayerScreen::class.java) {

    val screenBetterSlotListAccountManager = ScreenBetterSlotListAccountManager()

    init {
        add("Account Manager") {
            MinecraftClient.getInstance().setScreen(screenBetterSlotListAccountManager.apply { prevScreen = MinecraftClient.getInstance().currentScreen })
        }
    }
}