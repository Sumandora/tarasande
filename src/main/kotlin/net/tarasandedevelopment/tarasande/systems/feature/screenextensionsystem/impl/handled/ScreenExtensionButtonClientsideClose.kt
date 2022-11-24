package net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl.handled

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.DeathScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.ingame.LecternScreen
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.ScreenExtensionButton

class ScreenExtensionButtonClientsideClose : ScreenExtensionButton<Screen>("Clientside close", HandledScreen::class.java, LecternScreen::class.java, DeathScreen::class.java) {

    override fun onClick(current: Screen) {
        MinecraftClient.getInstance().setScreen(null)
    }
}