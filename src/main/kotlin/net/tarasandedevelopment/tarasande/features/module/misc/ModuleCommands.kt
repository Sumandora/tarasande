package net.tarasandedevelopment.tarasande.features.module.misc

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.Text
import net.minecraft.text.Texts
import net.minecraft.util.Formatting
import net.tarasandedevelopment.tarasande.base.features.command.ManagerCommand
import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventChat
import net.tarasandedevelopment.tarasande.event.EventInputSuggestions
import net.tarasandedevelopment.tarasande.util.player.chat.CustomChat
import net.tarasandedevelopment.tarasande.value.ValueBind
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueText
import org.lwjgl.glfw.GLFW

class ModuleCommands : Module("Commands", "Client-sided commands for certain actions", ModuleCategory.MISC) {

    val prefix = ValueText(this, "Prefix", "$")
    private val exceptions = ValueBoolean(this, "Show exceptions", true)
    private val bypassCommands = object : ValueBind(this, "Bypass commands", Type.KEY, GLFW.GLFW_KEY_UNKNOWN) {
        override fun filter(type: Type, bind: Int) = bind >= GLFW.GLFW_KEY_ESCAPE || bind == GLFW.GLFW_KEY_UNKNOWN
    }

    private val commandSource = ClientCommandSource(null, MinecraftClient.getInstance()) // yep, this works lmao
    private val managerCommand = ManagerCommand()

    init {
        registerEvent(EventChat::class.java) {
            if (bypassCommands.isPressed(true)) return@registerEvent

            if (it.chatMessage.startsWith(prefix.value)) {
                it.cancelled = true

                val reader = StringReader(it.chatMessage)
                reader.cursor = prefix.value.length

                try {
                    managerCommand.dispatcher.execute(reader, this.commandSource)
                } catch (e: CommandSyntaxException) {
                    if (!exceptions.value) return@registerEvent

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

        registerEvent(EventInputSuggestions::class.java) {
            if (it.reader.canRead(prefix.value.length) && it.reader.string.startsWith(prefix.value, it.reader.cursor)) {
                it.reader.cursor = it.reader.cursor + prefix.value.length

                it.dispatcher = managerCommand.dispatcher
                it.commandSource = commandSource
            }
        }
    }
}
