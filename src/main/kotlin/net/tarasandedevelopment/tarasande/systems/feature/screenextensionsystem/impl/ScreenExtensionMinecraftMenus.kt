package net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.*
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.ScreenExtension
import net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.impl.accountmanager.ScreenBetterSlotListAccountManager
import net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.impl.proxy.ScreenBetterProxy

class ScreenExtensionMinecraftMenusSleepingChat : ScreenExtension("Client wakeup", SleepingChatScreen::class.java) {

    override fun onClick(current: Screen) {
        MinecraftClient.getInstance().player?.wakeUp()
        MinecraftClient.getInstance().setScreen(null)
    }
}

class ScreenExtensionMinecraftMenusDeath : ScreenExtension("Force respawn", DeathScreen::class.java) {

    override fun onClick(current: Screen) {
        MinecraftClient.getInstance().player?.requestRespawn()
        MinecraftClient.getInstance().setScreen(null)
    }
}
