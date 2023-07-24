package su.mandora.tarasande.util.render.framebuffer

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.SimpleFramebuffer
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventResolutionUpdate
import su.mandora.tarasande.mc

class SimpleFramebufferWrapped : SimpleFramebuffer(mc.window.framebufferWidth, mc.window.framebufferHeight, true, MinecraftClient.IS_SYSTEM_MAC) {

    init {
        setClearColor(0F, 0F, 0F, 0F)

        EventDispatcher.add(EventResolutionUpdate::class.java) {
            resize(mc.window.framebufferWidth, mc.window.framebufferHeight, MinecraftClient.IS_SYSTEM_MAC)
        }
    }

}