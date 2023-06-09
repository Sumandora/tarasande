package su.mandora.tarasande.system.feature.commandsystem.impl

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.PosArgument
import net.minecraft.command.argument.Vec3ArgumentType
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.feature.commandsystem.Command

class CommandClip : Command("clip") {

    override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
        return builder.then(argument("position", Vec3ArgumentType.vec3())?.executes {
            mc.player?.setPosition(it.getArgument("position", PosArgument::class.java).toAbsolutePos(createServerCommandSource()))
            return@executes SUCCESS
        })
    }
}
