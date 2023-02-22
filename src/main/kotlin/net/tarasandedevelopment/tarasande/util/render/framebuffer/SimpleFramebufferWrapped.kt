package net.tarasandedevelopment.tarasande.util.render.framebuffer

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.SimpleFramebuffer
import net.tarasandedevelopment.tarasande.event.EventDispatcher
import net.tarasandedevelopment.tarasande.event.impl.EventResolutionUpdate
import net.tarasandedevelopment.tarasande.mc

class SimpleFramebufferWrapped : SimpleFramebuffer(mc.window.framebufferWidth, mc.window.framebufferHeight, true, MinecraftClient.IS_SYSTEM_MAC) {

    init {
        setClearColor(0.0F, 0.0F, 0.0F, 0.0F)

        EventDispatcher.add(EventResolutionUpdate::class.java) {
            resize(mc.window.framebufferWidth, mc.window.framebufferHeight, MinecraftClient.IS_SYSTEM_MAC)
        }
    }

}