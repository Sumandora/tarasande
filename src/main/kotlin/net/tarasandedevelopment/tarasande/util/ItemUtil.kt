package net.tarasandedevelopment.tarasande.util

import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.util.chat.CustomChat

object ItemUtil {

    private val noCreative = Text.literal("You must be in creative mode to use this.")
    private val noSpace = Text.literal("No space in hotbar.")

    private val placed = Text.literal("The item was placed in your hotbar")

    fun give(stack: ItemStack) {
        if (!MinecraftClient.getInstance().player?.abilities!!.creativeMode) {
            CustomChat.print(this.noCreative)
            return
        }

        if (!MinecraftClient.getInstance().player?.inventory!!.insertStack(stack)) {
            CustomChat.print(this.noSpace)
        }

        CustomChat.print(this.placed)
    }
}
