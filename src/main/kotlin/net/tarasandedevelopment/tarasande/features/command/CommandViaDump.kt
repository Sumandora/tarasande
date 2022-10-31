package net.tarasandedevelopment.tarasande.features.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.command.ViaCommandSender
import net.minecraft.client.MinecraftClient
import net.minecraft.command.CommandSource
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.tarasandedevelopment.tarasande.base.features.command.Command
import net.tarasandedevelopment.tarasande.util.player.chat.CustomChat
import java.util.*

class CommandViaDump : Command("ViaDump") {

    private val sender = ViaDumpBypassSender()

    override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
        return builder.executes {
            Via.getManager().commandHandler.getSubCommand("dump")!!.execute(sender, arrayOf())
            return@executes 1
        }
    }

    class ViaDumpBypassSender : ViaCommandSender {
        override fun hasPermission(permission: String?): Boolean {
            return true
        }

        override fun sendMessage(msg: String?) {
            if (msg == null) return
            Formatting.strip(msg)?.let {
                CustomChat.print(Text.literal(it))
            }

            if (msg.contains("https://")) {
                MinecraftClient.getInstance().keyboard.clipboard = msg.split("https://")[1]
            }
        }

        override fun getUUID(): UUID = UUID.fromString(MinecraftClient.getInstance().session.uuid)
        override fun getName(): String = MinecraftClient.getInstance().session.username
    }
}
