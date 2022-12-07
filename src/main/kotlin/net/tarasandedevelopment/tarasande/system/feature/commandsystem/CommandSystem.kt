package net.tarasandedevelopment.tarasande.system.feature.commandsystem

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientCommandSource
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.command.CommandSource
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.Text
import net.minecraft.text.Texts
import net.minecraft.util.Formatting
import net.minecraft.util.registry.DynamicRegistryManager
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.event.EventChat
import net.tarasandedevelopment.tarasande.event.EventInputSuggestions
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBind
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueText
import net.tarasandedevelopment.tarasande.system.feature.commandsystem.impl.CommandEnchant
import net.tarasandedevelopment.tarasande.system.feature.commandsystem.impl.CommandFakeGameMode
import net.tarasandedevelopment.tarasande.system.feature.commandsystem.impl.CommandGive
import net.tarasandedevelopment.tarasande.system.feature.commandsystem.impl.CommandSay
import net.tarasandedevelopment.tarasande.util.player.chat.CustomChat
import org.lwjgl.glfw.GLFW
import su.mandora.event.EventDispatcher

class ManagerCommand : Manager<Command>() {

    private val commandPrefix = ValueText(this, "Command prefix", "$")
    private val exceptions = ValueBoolean(this, "Show exceptions", true)
    private val bypassCommands = object : ValueBind(this, "Bypass commands", Type.KEY, GLFW.GLFW_KEY_UNKNOWN) {
        override fun filter(type: Type, bind: Int) = bind >= GLFW.GLFW_KEY_ESCAPE || bind == GLFW.GLFW_KEY_UNKNOWN
    }

    private val dispatcher = CommandDispatcher<CommandSource>()
    private val commandSource = ClientCommandSource(null, MinecraftClient.getInstance()) // yep, this works lmao

    init {
        add(
            CommandSay(),
            CommandGive(),
            CommandEnchant(),
            CommandFakeGameMode()
        )

        EventDispatcher.add(EventChat::class.java) {
            if (bypassCommands.isPressed(true)) return@add

            if (it.chatMessage.startsWith(commandPrefix.value)) {
                it.cancelled = true

                val reader = StringReader(it.chatMessage)
                reader.cursor = commandPrefix.value.length

                try {
                    dispatcher.execute(reader, this.commandSource)
                } catch (e: CommandSyntaxException) {
                    if (!exceptions.value) return@add

                    CustomChat.printChatMessage(Text.literal("").formatted(Formatting.RED).append(Texts.toText(e.rawMessage)))

                    if (e.cursor >= 0) {
                        val index = e.input.length.coerceAtMost(e.cursor)
                        val verbose = Text.literal("").formatted(Formatting.GRAY).styled {
                            it.withClickEvent(ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, reader.string))
                        }

                        if (index < e.input.length) {
                            verbose.append(Text.literal(e.input.substring(index)).formatted(Formatting.RED, Formatting.UNDERLINE))
                        }

                        verbose.append(Text.translatable("command.context.here").formatted(Formatting.RED, Formatting.ITALIC))
                        CustomChat.printChatMessage(Text.literal("").formatted(Formatting.RED).append(Texts.toText(verbose)))
                    }
                }
            }
        }

        EventDispatcher.add(EventInputSuggestions::class.java) {
            if (it.reader.canRead(commandPrefix.value.length) && it.reader.string.startsWith(commandPrefix.value, it.reader.cursor)) {
                it.reader.cursor = it.reader.cursor + commandPrefix.value.length

                it.dispatcher = dispatcher
                it.commandSource = commandSource
            }
        }
    }

    override fun insert(obj: Command, index: Int) {
        super.insert(obj, index)
        obj.setup(dispatcher)
    }
}

abstract class Command(private vararg val aliases: String) {

    val mc = MinecraftClient.getInstance()

    companion object {
        val registryAccess = CommandRegistryAccess(DynamicRegistryManager.BUILTIN.get())
        val notInCreative = SimpleCommandExceptionType(Text.literal("You must be in creative mode to use this"))

        const val success = 1
        const val error = 0
    }

    fun printChatMessage(message: String) = CustomChat.printChatMessage(Text.literal(message))
    fun createServerCommandSource() = ServerCommandSource(null, MinecraftClient.getInstance().player?.pos, null, null, 0, null, null, null, null)

    open fun literal(name: String): LiteralArgumentBuilder<CommandSource> = LiteralArgumentBuilder.literal(name)
    open fun argument(name: String?, type: ArgumentType<*>?): RequiredArgumentBuilder<CommandSource?, *>? = RequiredArgumentBuilder.argument(name, type)

    abstract fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource>

    fun setup(dispatcher: CommandDispatcher<CommandSource>) {
        aliases.forEach { alias ->
            literal(alias).also {
                dispatcher.register(literal(alias).redirect(dispatcher.register(builder(it))))
            }
        }
    }
}
