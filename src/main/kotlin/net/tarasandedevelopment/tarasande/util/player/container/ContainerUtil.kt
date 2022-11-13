package net.tarasandedevelopment.tarasande.util.player.container

import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.*
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.util.math.Vec2f

object ContainerUtil {

    fun getDisplayPosition(original: HandledScreen<*>, slot: Slot): Vec2f {
        return Vec2f(original.x + slot.x.toFloat() + 8, original.y + slot.y.toFloat() + 8)
    }

    fun getValidSlots(screenHandler: ScreenHandler): List<Slot> {
        return screenHandler.slots
            .filter { it != null && it.isEnabled && it.hasStack() }
    }

    fun getClosestSlot(screenHandler: ScreenHandler, original: HandledScreen<*>, lastMouseClick: Vec2f, block: (Slot, List<Slot>) -> Boolean): Slot? {
        val list = getValidSlots(screenHandler)
        return list
            .filter { block.invoke(it, list) }
            .minByOrNull { lastMouseClick.distanceSquared(getDisplayPosition(original, it)) }
    }

    private fun wrapMaterialScore(stack: ItemStack, durability: Boolean): Float {
        return when (stack.item) {
            is SwordItem -> (stack.item as ToolItem).material.attackDamage
            is ToolItem -> (stack.item as ToolItem).material.durability.toFloat()
            is ArmorItem -> (stack.item as ArmorItem).material.getDurability((stack.item as ArmorItem).slotType).toFloat()
            else -> 0.0f
        }.times(if (durability) (1.0f - stack.damage / stack.maxDamage.toFloat()) else 1.0f)
    }

    fun isSameItemType(stack: ItemStack, otherStack: ItemStack): Boolean {
        return when {
            stack.item is SwordItem && otherStack.item is SwordItem -> true

            // There is no way to group them better
            stack.item is AxeItem && otherStack.item is AxeItem -> true
            stack.item is PickaxeItem && otherStack.item is PickaxeItem -> true
            stack.item is HoeItem && otherStack.item is HoeItem -> true
            stack.item is ShovelItem && otherStack.item is ShovelItem -> true

            stack.item is ArmorItem && otherStack.item is ArmorItem -> (stack.item as ArmorItem).slotType == (otherStack.item as ArmorItem).slotType

            else -> stack.item == otherStack.item
        }
    }

    fun hasBetterEquivalent(stack: ItemStack, list: List<ItemStack>, keepSameMaterial: Boolean, keepSameEnchantments: Boolean): Boolean {
        if (stack.item !is ToolItem && stack.item !is ArmorItem && stack.item !is FishingRodItem && stack.item !is BowItem)
            return false

        val materialScore = wrapMaterialScore(stack, false)

        val enchantments = EnchantmentHelper.get(stack)

        for (otherStack in list) {
            if (isSameItemType(stack, otherStack)) {
                val otherEnchantments = EnchantmentHelper.get(otherStack)

                val otherMaterialScore = wrapMaterialScore(otherStack, false)

                if ((if (keepSameMaterial) otherMaterialScore > materialScore else otherMaterialScore >= materialScore) && ((enchantments.isEmpty() && otherEnchantments.isNotEmpty()) || enchantments.all { otherEnchantments.containsKey(it.key) && (if (keepSameEnchantments) otherEnchantments[it.key]!! > it.value else otherEnchantments[it.key]!! >= it.value) })) {
//                    if (otherStack.damage > stack.damage * durability)
//                        continue

                    //println("$stack $otherStack $materialScore > $otherMaterialScore ${1.0f - stack.damage / stack.maxDamage.toFloat()} ${1.0f - otherStack.damage / otherStack.maxDamage.toFloat()} ${enchantments.isEmpty() && otherEnchantments.isNotEmpty()} ${((materialScore == null || otherMaterialScore == null) || (if (keepSameMaterial) otherMaterialScore > materialScore else otherMaterialScore >= materialScore))}")
                    return true
                }
            }
        }

        return false
    }
}