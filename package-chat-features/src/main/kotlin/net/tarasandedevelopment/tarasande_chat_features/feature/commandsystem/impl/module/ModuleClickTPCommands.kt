package net.tarasandedevelopment.tarasande_chat_features.feature.commandsystem.impl.module

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.BlockPosArgumentType
import net.minecraft.command.argument.PosArgument
import net.minecraft.command.argument.Vec3ArgumentType
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleClickTP
import net.tarasandedevelopment.tarasande_chat_features.feature.commandsystem.Command

class CommandTeleport : Command("teleport", "tp") {
    override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
        return builder.then(argument("position", BlockPosArgumentType.blockPos())?.executes {
            ManagerModule.get(ModuleClickTP::class.java).teleportToPosition(it.getArgument("position", PosArgument::class.java).toAbsoluteBlockPos(createServerCommandSource()))
            return@executes SUCCESS
        })
    }
}

class CommandClip : Command("clip") {
    override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
        return builder.then(argument("position", Vec3ArgumentType.vec3())?.executes {
            mc.player?.setPosition(it.getArgument("position", PosArgument::class.java).toAbsolutePos(createServerCommandSource()))
            return@executes SUCCESS
        })
    }
}
