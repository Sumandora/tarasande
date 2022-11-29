package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList

class ScreenExtensionButtonListHandledScreen : ScreenExtensionButtonList<HandledScreen<*>>(HandledScreen::class.java) {

    init {
        add("Clientside close") {
            MinecraftClient.getInstance().setScreen(null)
        }
        add("Serverside close") {
            MinecraftClient.getInstance().networkHandler?.sendPacket(CloseHandledScreenC2SPacket(MinecraftClient.getInstance().player?.currentScreenHandler!!.syncId))
        }
    }
}
