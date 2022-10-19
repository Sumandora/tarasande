package net.tarasandedevelopment.tarasande.util.player.container

import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.*
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.util.math.Vec2f
import net.tarasandedevelopment.tarasande.mixin.accessor.IArmorMaterials
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

    private fun wrapMaterialScore(stack: ItemStack): Float? {
        return when (stack.item) {
            is SwordItem -> (stack.item as ToolItem).material.attackDamage
            is ToolItem -> (stack.item as ToolItem).material.durability.toFloat()
            is ArmorItem -> ((stack.item as ArmorItem).material as IArmorMaterials).tarasande_getDurabilityMultiplier().toFloat()
            else -> null
        }
    }

    private fun isSameItemType(stack: ItemStack, otherStack: ItemStack): Boolean {
        return when {
            stack.item is ToolItem && otherStack.item is ToolItem -> true
            stack.item is ArmorItem && otherStack.item is ArmorItem -> true
            else -> stack.item == otherStack.item
        }
    }

    fun hasBetterEquivalent(stack: ItemStack, list: List<ItemStack>): Boolean {
        if (stack.item !is ToolItem && stack.item !is ArmorItem && stack.item !is FishingRodItem && stack.item !is BowItem)
            return false

        val materialScore = wrapMaterialScore(stack)

        val enchantments = EnchantmentHelper.get(stack)

        for (otherStack in list) {
            if (isSameItemType(stack, otherStack)) {
                val otherEnchantments = EnchantmentHelper.get(otherStack)

                val otherMaterialScore = wrapMaterialScore(otherStack)

                if (((materialScore == null || otherMaterialScore == null) || otherMaterialScore > materialScore) && enchantments.all { otherEnchantments.containsKey(it.key) && otherEnchantments[it.key]!! >= it.value })
                    return true
            }
        }

        return false
    }
}