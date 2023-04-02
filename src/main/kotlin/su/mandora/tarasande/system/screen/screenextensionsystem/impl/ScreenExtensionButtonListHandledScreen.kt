package su.mandora.tarasande.system.screen.screenextensionsystem.impl

import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList

class ScreenExtensionButtonListHandledScreen : ScreenExtensionButtonList<HandledScreen<*>>(HandledScreen::class.java) {

    init {
        add("Clientside close") {
            mc.setScreen(null)
        }
        add("Serverside close") {
            mc.networkHandler?.sendPacket(CloseHandledScreenC2SPacket(mc.player?.currentScreenHandler!!.syncId))
        }
    }
}
