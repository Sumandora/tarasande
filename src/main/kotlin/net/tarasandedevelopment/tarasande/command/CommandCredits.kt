package net.tarasandedevelopment.tarasande.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.command.CommandSource
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.base.command.Command
import net.tarasandedevelopment.tarasande.util.player.chat.CustomChat

class CommandCredits : Command("Credits") {

    override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
        return builder.executes {
            CustomChat.print(Text.literal("tarasande by florianmichael and sumandora"))
            return@executes 1
        }
    }
}