package net.tarasandedevelopment.tarasande.base.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientCommandSource
import net.minecraft.command.CommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.Text
import net.minecraft.text.Texts
import net.minecraft.util.Formatting
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.command.CommandCredits
import net.tarasandedevelopment.tarasande.command.CommandViaDump
import net.tarasandedevelopment.tarasande.event.EventChat
import net.tarasandedevelopment.tarasande.module.misc.ModuleCommands
import net.tarasandedevelopment.tarasande.util.player.chat.CustomChat

class ManagerCommand : Manager<Command>() {

    val dispatcher = CommandDispatcher<CommandSource>()

    init {
        add(
            CommandViaDump(),
            CommandCredits()
        )

        list.forEach {
            it.setup(dispatcher)
        }
    }
}

abstract class Command(val name: String) {

    open fun literal(name: String): LiteralArgumentBuilder<CommandSource> = LiteralArgumentBuilder.literal(name)
    open fun argument(name: String?, type: ArgumentType<*>?): RequiredArgumentBuilder<CommandSource?, *>? = RequiredArgumentBuilder.argument(name, type)

    fun setup(dispatcher: CommandDispatcher<CommandSource>) {
        val base = this.literal(this.name)

        val literal = dispatcher.register(builder(base))
        dispatcher.register(literal(name).redirect(literal))
    }

    abstract fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource>
}
