package net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.DeathScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.ingame.LecternScreen
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.ScreenExtensionButton

class ScreenExtensionButtonScreenExtensionHandledScreensClientsideClose : ScreenExtensionButton<Screen>("Clientside close", HandledScreen::class.java, LecternScreen::class.java, DeathScreen::class.java) {

    override fun onClick(current: Screen) {
        MinecraftClient.getInstance().setScreen(null)
    }
}

class ScreenExtensionButtonScreenExtensionHandledScreensServersideClose : ScreenExtensionButton<Screen>("Serverside close", HandledScreen::class.java, LecternScreen::class.java) {

    override fun onClick(current: Screen) {
        MinecraftClient.getInstance().networkHandler?.sendPacket(CloseHandledScreenC2SPacket(MinecraftClient.getInstance().player?.currentScreenHandler!!.syncId))
    }
}
