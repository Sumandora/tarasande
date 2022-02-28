package su.mandora.tarasande.screen.menu.information

import net.minecraft.client.MinecraftClient
import su.mandora.tarasande.base.screen.menu.information.Information
import su.mandora.tarasande.mixin.accessor.IClientConnection

class InformationHandlers : Information("Connection", "Handlers") {
    override fun getMessage(): String? {
        if (MinecraftClient.getInstance().networkHandler == null || MinecraftClient.getInstance().networkHandler?.connection == null)
            return null
        val names = ((MinecraftClient.getInstance().networkHandler?.connection as IClientConnection).channel ?: return null).pipeline().names()
        if (names.isEmpty())
            return null
        val asString = names.subList(0, names.size - 1).toTypedArray().contentToString()
        return "\n" + asString.substring(1, asString.length - 1).replace(", ", "\n")
    }
}