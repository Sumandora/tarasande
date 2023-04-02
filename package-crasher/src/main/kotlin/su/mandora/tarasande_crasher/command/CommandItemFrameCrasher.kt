package su.mandora.tarasande_crasher.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.command.CommandSource
import su.mandora.tarasande.system.feature.commandsystem.Command
import su.mandora.tarasande.util.player.PlayerUtil

class CommandItemFrameCrasher : Command("itemframecrasher") {

    override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
        return builder.executes {
            PlayerUtil.sendChatMessage("/setblock ~ ~-1 ~ minecraft:mob_spawner 0 replace {EntityId:ItemFrame}", true)
            return@executes SUCCESS
        }
    }
}