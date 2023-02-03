package net.tarasandedevelopment.tarasande.system.feature.commandsystem.impl

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.ItemStackArgument
import net.minecraft.command.argument.ItemStackArgumentType
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.feature.commandsystem.Command
import net.tarasandedevelopment.tarasande.util.player.chat.CustomChat

class CommandGive : Command("give") {

    private fun executeGive(item: ItemStackArgument, count: Int = 1): Int {
        if (!mc.player?.abilities?.creativeMode!!) {
            throw notInCreative.create()
        }
        item.createStack(count.coerceAtMost(item.item.maxCount), false).also {
            mc.interactionManager?.clickCreativeStack(it, 36 + (mc.player?.inventory?.selectedSlot ?: 0))
            mc.player?.playerScreenHandler?.sendContentUpdates()

            CustomChat.printChatMessage("You have received the item " + it.toHoverableText().string)
        }
        return SUCCESS
    }

    override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
        return builder.then(argument("item", ItemStackArgumentType.itemStack(registryAccess))?.executes {
            return@executes executeGive(ItemStackArgumentType.getItemStackArgument(it, "item"))
        }?.then(argument("count", IntegerArgumentType.integer())?.executes {
            return@executes executeGive(ItemStackArgumentType.getItemStackArgument(it, "item"), IntegerArgumentType.getInteger(it, "count"))
        }))
    }
}
