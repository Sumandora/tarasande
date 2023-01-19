package net.tarasandedevelopment.tarasande_chat_features.feature.commandsystem.impl

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.command.CommandSource
import net.minecraft.world.GameMode
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande_chat_features.feature.commandsystem.Command

class CommandFakeGameMode : Command("fakegamemode") {

    override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
        GameMode.values().forEach { gameMode ->
            builder.then(literal(gameMode.getName()).executes {
                mc.interactionManager?.setGameMode(gameMode)
                return@executes SUCCESS
            })
        }
        return builder
    }
}
