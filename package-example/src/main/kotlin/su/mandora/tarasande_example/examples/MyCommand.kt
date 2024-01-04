package su.mandora.tarasande_example.examples

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.command.CommandSource
import su.mandora.tarasande.system.feature.commandsystem.Command
import su.mandora.tarasande.util.player.chat.CustomChat

class MyCommand : Command("mycommand") {
    override fun builder(builder: LiteralArgumentBuilder<CommandSource>) {
        builder.executes {
            CustomChat.printChatMessage("Hello, world!")
            return@executes SUCCESS
        }
    }
}
