package net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl.accountmanager.screenextension

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.DirectConnectScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.ScreenExtensionButton
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl.accountmanager.ScreenBetterSlotListAccountManager

class ScreenExtensionButtonAccountManager : ScreenExtensionButton<Screen>("Account Manager", MultiplayerScreen::class.java, DirectConnectScreen::class.java) {

    val screenBetterSlotListAccountManager = ScreenBetterSlotListAccountManager()

    override fun onClick(current: Screen) {
        MinecraftClient.getInstance().setScreen(this.screenBetterSlotListAccountManager.apply { prevScreen = MinecraftClient.getInstance().currentScreen })
    }
}
