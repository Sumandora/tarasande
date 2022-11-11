package net.tarasandedevelopment.tarasande.features.screenextension

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.DeathScreen
import net.minecraft.client.gui.screen.GameMenuScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.SleepingChatScreen
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.gui.screen.ingame.SignEditScreen
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.features.screenextension.ScreenExtension
import net.tarasandedevelopment.tarasande.screen.clientmenu.ScreenBetterSlotListClientMenu

class ScreenExtensionMinecraftMenusClientMenu : ScreenExtension(TarasandeMain.get().name.let { it[0].uppercaseChar().toString() + it.substring(1) + " Menu" }, TitleScreen::class.java, GameMenuScreen::class.java) {

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

class ScreenExtensionMinecraftMenusSignEdit : ScreenExtension("Clientside close", SignEditScreen::class.java) {

    override fun onClick(current: Screen) {
        MinecraftClient.getInstance().setScreen(null)
    }
}

class ScreenExtensionMinecraftMenusDeath : ScreenExtension("Force respawn", DeathScreen::class.java) {

    override fun onClick(current: Screen) {
        MinecraftClient.getInstance().player?.requestRespawn()
        MinecraftClient.getInstance().setScreen(null)
    }
}