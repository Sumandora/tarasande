package net.tarasandedevelopment.tarasande_protocol_hack.util.values.command

import com.viaversion.viaversion.api.command.ViaCommandSender
import net.minecraft.client.MinecraftClient

object ViaDumpBypassSender : ViaCommandSender {

    override fun hasPermission(permission: String?): Boolean {
        return true
    }

    override fun sendMessage(msg: String?) {
        if (msg == null) return
        if (msg.contains("https://")) {
            MinecraftClient.getInstance().keyboard.clipboard = msg.split("https://")[1]
        }
    }

    override fun getUUID() = MinecraftClient.getInstance().session.uuidOrNull
    override fun getName(): String = MinecraftClient.getInstance().session.username
}
