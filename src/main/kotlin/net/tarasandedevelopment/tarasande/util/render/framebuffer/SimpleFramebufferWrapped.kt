package net.tarasandedevelopment.tarasande.util.render.framebuffer

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.SimpleFramebuffer
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventResolutionUpdate

class SimpleFramebufferWrapped : SimpleFramebuffer(MinecraftClient.getInstance().window.framebufferWidth, MinecraftClient.getInstance().window.framebufferHeight, true, MinecraftClient.IS_SYSTEM_MAC) {

    init {
        setClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        TarasandeMain.get().eventDispatcher.add(EventResolutionUpdate::class.java) {
            resize(MinecraftClient.getInstance().window.framebufferWidth, MinecraftClient.getInstance().window.framebufferHeight, MinecraftClient.IS_SYSTEM_MAC)
        }
    }

}