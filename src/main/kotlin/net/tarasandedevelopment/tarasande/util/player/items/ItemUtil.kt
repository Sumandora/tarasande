package net.tarasandedevelopment.tarasande.util.player.items

import net.minecraft.client.MinecraftClient
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.*
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.util.player.chat.CommunicationUtil
import net.tarasandedevelopment.tarasande.util.string.StringUtil

object ItemUtil {

    private const val noCreative = "You must be in creative mode to use this."
    private const val noSpace = "No space in hotbar."

    private const val placed = "The item was placed in your hotbar"

    fun give(stack: ItemStack) {
        if (!MinecraftClient.getInstance().player?.abilities!!.creativeMode) {
            CommunicationUtil.printInformation(noCreative)
            return
        }

        if (!MinecraftClient.getInstance().player?.inventory!!.insertStack(stack)) {
            CommunicationUtil.printInformation(noSpace)
        }

        CommunicationUtil.printInformation(placed)
    }

    fun enchantSimpleName(enchantment: Enchantment, length: Int) = enchantment.getName(0).string.substring(0, length)

    // ReinerWahnsinn Spawner Bypass for Spigot/CraftBukkit 1.8.3
    fun packageExploit(stack: ItemStack): ItemStack {
        val packager = TarasandeMain.get().screenCheatMenu.managerCreative.getPackagedItem() ?: return stack

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
