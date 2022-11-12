package net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.ingame.LecternScreen
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.ScreenExtension

class ScreenExtensionHandledScreensClientsideClose : ScreenExtension("Clientside close", HandledScreen::class.java, LecternScreen::class.java) {

    override fun onClick(current: Screen) {
        MinecraftClient.getInstance().setScreen(null)
    }
}

class ScreenExtensionHandledScreensServersideClose : ScreenExtension("Serverside close", HandledScreen::class.java, LecternScreen::class.java) {

    override fun onClick(current: Screen) {
        MinecraftClient.getInstance().networkHandler?.sendPacket(CloseHandledScreenC2SPacket(MinecraftClient.getInstance().player?.currentScreenHandler!!.syncId))
    }
}
