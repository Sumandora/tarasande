package net.tarasandedevelopment.tarasande_protocol_hack.util.values.command

import com.viaversion.viaversion.api.command.ViaCommandSender
import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.util.extension.mc

object ViaDumpBypassSender : ViaCommandSender {

    override fun hasPermission(permission: String?): Boolean {
        return true
    }

    override fun sendMessage(msg: String?) {
        if (msg == null) return
        if (msg.contains("https://")) {
            mc.keyboard.clipboard = msg.split("https://")[1]
        }
    }

    override fun getUUID() = mc.session.uuidOrNull
    override fun getName(): String = mc.session.username
}
