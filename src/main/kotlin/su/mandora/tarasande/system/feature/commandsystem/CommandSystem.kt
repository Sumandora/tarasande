package su.mandora.tarasande.system.feature.commandsystem

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.client.network.ClientCommandSource
import net.minecraft.client.network.ClientDynamicRegistryType
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.command.CommandSource
import net.minecraft.resource.featuretoggle.FeatureFlags
import net.minecraft.screen.ScreenTexts
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.Text
import net.minecraft.text.Texts
import net.minecraft.util.Formatting
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventChat
import su.mandora.tarasande.event.impl.EventInputSuggestions
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBind
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueText
import su.mandora.tarasande.system.feature.commandsystem.impl.*
import su.mandora.tarasande.system.feature.modulesystem.command.CommandToggle
import su.mandora.tarasande.util.player.chat.CustomChat
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.Manager
import kotlin.math.max

object ManagerCommand : Manager<Command>() {

    private val commandPrefix = ValueText(this, "Command prefix", "$")
    private val exceptions = ValueBoolean(this, "Show exceptions", true)
    private val bypassCommands = object : ValueBind(this, "Bypass commands", Type.KEY, GLFW.GLFW_KEY_UNKNOWN) {
        override fun filter(type: Type, bind: Int) = bind >= GLFW.GLFW_KEY_ESCAPE || bind == GLFW.GLFW_KEY_UNKNOWN
    }

    private val dispatcher = CommandDispatcher<CommandSource>()
    private val commandSource = ClientCommandSource(null, mc) // yep, this works lmao

    init {
        add(
            CommandSay(),
            CommandGive(),
            CommandEnchant(),
            CommandFakeGameMode(),
            CommandClip(),
            CommandToggle()
        )

        EventDispatcher.add(EventChat::class.java) {
            if (it.chatMessage.startsWith(commandPrefix.value)) {
                if (bypassCommands.isPressed(true)) return@add

                it.cancelled = true

                val reader = StringReader(it.chatMessage)
                reader.cursor = commandPrefix.value.length

                try {
                    dispatcher.execute(reader, commandSource)
                } catch (commandSyntaxException: CommandSyntaxException) {
                    if (!exceptions.value) return@add

                    CustomChat.printChatMessage(Text.literal("").formatted(Formatting.RED).append(Texts.toText(commandSyntaxException.rawMessage)))

                    if (commandSyntaxException.cursor >= 0) {
                        val i = commandSyntaxException.input.length.coerceAtMost(commandSyntaxException.cursor)
                        val mutableText = Text.empty().formatted(Formatting.GRAY).styled { style -> style.withClickEvent(ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, it.chatMessage)) }
                        if (i > 10) {
                            mutableText.append(ScreenTexts.ELLIPSIS)
                        }
                        mutableText.append(commandSyntaxException.input.substring(max(0, i - 10), i))
                        if (i < commandSyntaxException.input.length) {
                            val text = Text.literal(commandSyntaxException.input.substring(i)).formatted(Formatting.RED, Formatting.UNDERLINE)
                            mutableText.append(text)
                        }
                        mutableText.append(Text.translatable("command.context.here").formatted(Formatting.RED, Formatting.ITALIC))
                        CustomChat.printChatMessage(Text.literal("").formatted(Formatting.RED).append(Texts.toText(mutableText)))
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

    companion object {
        val registryAccess: CommandRegistryAccess = CommandRegistryAccess.of(ClientDynamicRegistryType.createCombinedDynamicRegistries().combinedRegistryManager, FeatureFlags.DEFAULT_ENABLED_FEATURES)
        val notInCreative = SimpleCommandExceptionType(Text.literal("You must be in creative mode to use this"))

        const val SUCCESS = 1
        const val ERROR = 0
    }

    fun createServerCommandSource() = ServerCommandSource(null, mc.player?.pos, null, null, 0, null, null, null, null)

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
