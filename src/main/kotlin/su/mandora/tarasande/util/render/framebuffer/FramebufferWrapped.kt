package su.mandora.tarasande.util.render.framebuffer

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.SimpleFramebuffer
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.event.EventResolutionUpdate

class FramebufferWrapped : SimpleFramebuffer(MinecraftClient.getInstance().window.framebufferWidth, MinecraftClient.getInstance().window.framebufferHeight, true, MinecraftClient.IS_SYSTEM_MAC) {

    init {
        setClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        TarasandeMain.get().managerEvent?.add { event ->
            if (event is EventResolutionUpdate) {
                resize(MinecraftClient.getInstance().window.framebufferWidth.toInt(), MinecraftClient.getInstance().window.framebufferHeight.toInt(), MinecraftClient.IS_SYSTEM_MAC)
            }
        }
    }

}