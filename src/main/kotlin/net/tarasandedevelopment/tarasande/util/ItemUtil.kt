package net.tarasandedevelopment.tarasande.util

import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtByte
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtShort
import net.minecraft.nbt.NbtString
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.util.chat.CustomChat
import net.tarasandedevelopment.tarasande.util.string.StringUtil

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

    // ReinerWahnsinn Spawner Bypass for Spigot/CraftBukkit 1.8.3
    fun packageExploit(stack: ItemStack): ItemStack {
        val packager = TarasandeMain.get().managerCreative.globalOwner.getPackagedItem() ?: return stack

        val packagedItem = ItemStack(packager.item)

        val base = NbtCompound()
        val blockEntityTag = NbtCompound()

        if (packager.item == Items.FURNACE) {
            blockEntityTag.put("BurnTime", NbtShort.of(0))
            blockEntityTag.put("CookTime", NbtShort.of(0))
            blockEntityTag.put("CookTimeTotal", NbtShort.of(0))
        }

        blockEntityTag.put("id", NbtString.of(StringUtil.uncoverTranslation(stack.translationKey).replace(" ", "")))

        if (packager.item == Items.FURNACE)
            blockEntityTag.put("Lock", NbtString.of(""))

        val items = NbtList()

        val bypassItem = NbtCompound()
        bypassItem.put("Count", NbtByte.of(1))
        bypassItem.put("Damage", NbtShort.of(stack.damage.toShort()))
        bypassItem.put("id", NbtString.of(StringUtil.uncoverTranslation(stack.translationKey).replace(" ", "")))
        bypassItem.put("Slot", NbtShort.of(0))
        bypassItem.put("tag", stack.nbt)

        items.add(bypassItem)

        blockEntityTag.put("Items", items)
        base.put("BlockEntityTag", blockEntityTag)

        packagedItem.nbt = base

        return packagedItem
    }
}
