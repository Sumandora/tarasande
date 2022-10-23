package net.tarasandedevelopment.tarasande.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.command.CommandSource
import net.tarasandedevelopment.tarasande.base.command.Command
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil

class CommandSay : Command("Say") {

    override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
        return builder.then(this.argument("message", StringArgumentType.greedyString())?.executes {
            val message = StringArgumentType.getString(it, "messsage")
            PlayerUtil.sendChatMessage(message, false)
            return@executes 1
        })
    }
}
