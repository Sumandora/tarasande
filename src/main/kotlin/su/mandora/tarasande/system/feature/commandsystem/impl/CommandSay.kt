package su.mandora.tarasande.system.feature.commandsystem.impl

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.command.CommandSource
import su.mandora.tarasande.system.feature.commandsystem.Command
import su.mandora.tarasande.util.player.PlayerUtil

class CommandSay : Command("say") {

    override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
        return builder.then(argument("message", StringArgumentType.greedyString())?.executes {
            PlayerUtil.sendChatMessage(StringArgumentType.getString(it, "message"), true)
            return@executes SUCCESS
        })
    }
}
