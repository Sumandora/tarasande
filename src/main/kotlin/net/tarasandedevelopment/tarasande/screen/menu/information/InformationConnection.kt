package net.tarasandedevelopment.tarasande.screen.menu.information

import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.base.screen.menu.information.Information
import net.tarasandedevelopment.tarasande.mixin.accessor.IClientConnection

class InformationHandlers : Information("Connection", "Handlers") {
    override fun getMessage(): String? {
        if (MinecraftClient.getInstance().networkHandler == null || MinecraftClient.getInstance().networkHandler?.connection == null) return null
        val names = ((MinecraftClient.getInstance().networkHandler?.connection as IClientConnection).tarasande_getChannel() ?: return null).pipeline().names()
        if (names.isEmpty()) return null
        return "\n" + names.subList(0, names.size - 1).joinToString("\n")
    }
}