package su.mandora.tarasande.system.feature.commandsystem.impl

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.command.CommandSource
import net.minecraft.world.GameMode
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.feature.commandsystem.Command

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
