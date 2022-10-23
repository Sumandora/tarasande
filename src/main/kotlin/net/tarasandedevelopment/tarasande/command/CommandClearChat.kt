package net.tarasandedevelopment.tarasande.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.client.MinecraftClient
import net.minecraft.command.CommandSource
import net.tarasandedevelopment.tarasande.base.command.Command

class CommandClearChat : Command("ClearChat") {

    override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
        return builder.executes {
            MinecraftClient.getInstance().inGameHud.chatHud.clear(true)
            return@executes 1
        }
    }
}
