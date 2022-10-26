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
import net.tarasandedevelopment.tarasande.command.CommandClearChat
import net.tarasandedevelopment.tarasande.command.CommandViaDump
import net.tarasandedevelopment.tarasande.event.EventChat
import net.tarasandedevelopment.tarasande.util.player.chat.CustomChat

class ManagerCommand : Manager<Command>() {

    val dispatcher = CommandDispatcher<CommandSource>()
    val commandSource = ClientCommandSource(null, MinecraftClient.getInstance()) // yep, this works lmao

    init {
        add(
            CommandViaDump(),
            CommandClearChat()
        )

        list.forEach {
            it.setup(dispatcher)
        }

        TarasandeMain.get().eventDispatcher.add(EventChat::class.java) {
            if (!TarasandeMain.get().clientValues.commands.value || TarasandeMain.get().clientValues.bypassCommands.isPressed(true)) return@add

            if (it.chatMessage.startsWith(TarasandeMain.get().clientValues.commandsPrefix.value)) {
                it.cancelled = true

                val reader = StringReader(it.chatMessage)
                reader.cursor = TarasandeMain.get().clientValues.commandsPrefix.value.length

                try {
                    dispatcher.execute(reader, this.commandSource)
                } catch (e: CommandSyntaxException) {
                    if (!TarasandeMain.get().clientValues.commandsExceptions.value) return@add

                    CustomChat.print(Text.literal("").formatted(Formatting.RED).append(Texts.toText(e.rawMessage)))

                    if (e.cursor >= 0) {
                        val index = e.input.length.coerceAtMost(e.cursor)
                        val verbose = Text.literal("").formatted(Formatting.GRAY).styled {
                            it.withClickEvent(ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, reader.string))
                        }

                        if (index < e.input.length) {
                            verbose.append(Text.literal(e.input.substring(index)).formatted(Formatting.RED, Formatting.UNDERLINE))
                        }

                        verbose.append(Text.translatable("command.context.here").formatted(Formatting.RED, Formatting.ITALIC))
                        CustomChat.print(Text.literal("").formatted(Formatting.RED).append(Texts.toText(verbose)))
                    }
                }
            }
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
