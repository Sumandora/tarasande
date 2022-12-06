package net.tarasandedevelopment.tarasande.system.feature.commandsystem.impl

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.ItemStackArgument
import net.minecraft.command.argument.ItemStackArgumentType
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.tarasandedevelopment.tarasande.system.feature.commandsystem.Command
import kotlin.jvm.Throws

class CommandGive : Command("give") {

    @Throws(CommandSyntaxException::class)
    private fun executeGive(item: ItemStackArgument, count: Int = 1): Int {
        if (!mc.player?.abilities?.creativeMode!!) {
            throw notInCreative.create()
        }
        item.createStack(count.coerceAtMost(item.item.maxCount), false).also {
            mc.interactionManager?.clickCreativeStack(it, 36 + (mc.player?.inventory?.selectedSlot ?: 0))
            mc.player?.playerScreenHandler?.sendContentUpdates()

            printChatMessage("You have received the item " + it.toHoverableText().string)
        }
        return success
    }

    override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
        return builder.then(argument("item", ItemStackArgumentType.itemStack(registryAccess))?.executes {
            return@executes executeGive(ItemStackArgumentType.getItemStackArgument(it, "item"))
        }?.then(argument("count", IntegerArgumentType.integer())?.executes {
            return@executes executeGive(ItemStackArgumentType.getItemStackArgument(it, "item"), IntegerArgumentType.getInteger(it, "count"))
        }))
    }
}
