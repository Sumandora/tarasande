package net.tarasandedevelopment.tarasande_chat_features.feature.commandsystem.impl.module

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.BlockPosArgumentType
import net.minecraft.command.argument.PosArgument
import net.minecraft.command.argument.Vec3ArgumentType
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleClickTP
import net.tarasandedevelopment.tarasande_chat_features.feature.commandsystem.Command
import net.tarasandedevelopment.tarasande_chat_features.feature.commandsystem.ManagerCommand

object ModuleClickTPCommands {

    fun init(clickTP: ModuleClickTP) {
        ManagerCommand.apply {
            add(object : Command("teleport", "tp") {
                override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
                    return builder.then(argument("position", BlockPosArgumentType.blockPos())?.executes {
                        clickTP.teleportToPosition(it.getArgument("position", PosArgument::class.java).toAbsoluteBlockPos(createServerCommandSource()))
                        return@executes SUCCESS
                    })
                }
            })
            add(object : Command("clip") {
                override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
                    return builder.then(argument("position", Vec3ArgumentType.vec3())?.executes {
                        mc.player?.setPosition(it.getArgument("position", PosArgument::class.java).toAbsolutePos(createServerCommandSource()))
                        return@executes SUCCESS
                    })
                }
            })
        }
    }
}
