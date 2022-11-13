package net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.*
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.ScreenExtension
import net.tarasandedevelopment.tarasande.systems.screen.clientmenu.screen.ScreenBetterSlotListClientMenu

class ScreenExtensionMinecraftMenusClientMenu : ScreenExtension(TarasandeMain.instance.name.let { it[0].uppercaseChar().toString() + it.substring(1) + " Menu" }, TitleScreen::class.java, MultiplayerScreen::class.java, GameMenuScreen::class.java) {

    override fun onClick(current: Screen) {
        MinecraftClient.getInstance().setScreen(ScreenBetterSlotListClientMenu(current))
    }
}

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