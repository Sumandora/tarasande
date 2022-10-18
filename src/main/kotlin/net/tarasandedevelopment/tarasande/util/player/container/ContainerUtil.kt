package net.tarasandedevelopment.tarasande.util.player.container

import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolItem
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.util.math.Vec2f
import net.tarasandedevelopment.tarasande.mixin.accessor.IHandledScreen

object ContainerUtil {

    fun getDisplayPosition(accessor: IHandledScreen, slot: Slot): Vec2f {
        return Vec2f(accessor.tarasande_getX() + slot.x.toFloat() + 8, accessor.tarasande_getY() + slot.y.toFloat() + 8)
    }

    fun getValidSlots(screenHandler: ScreenHandler): List<Slot> {
        return screenHandler.slots
            .filter { it != null && it.isEnabled && it.hasStack() }
    }

    fun getClosestSlot(screenHandler: ScreenHandler, accessor: IHandledScreen, lastMouseClick: Vec2f, block: (Slot, List<Slot>) -> Boolean): Slot? {
        val list = getValidSlots(screenHandler)
        return list
            .filter { block.invoke(it, list) }
            .minByOrNull { lastMouseClick.distanceSquared(getDisplayPosition(accessor, it)) }
    }

    fun hasBetterEquivalent(stack: ItemStack, list: List<ItemStack>): Boolean {
        if (stack.item !is SwordItem && stack.item !is ToolItem && stack.item !is ArmorItem)
            return false

        val enchantments = EnchantmentHelper.get(stack)

        for (otherStack in list) {
            if (stack.item == otherStack.item) {
                val otherEnchantments = EnchantmentHelper.get(otherStack)

                if (enchantments.all { otherEnchantments.containsKey(it.key) && enchantments[it.key]!! >= it.value })
                    return true
            }
        }

        return false
    }
}