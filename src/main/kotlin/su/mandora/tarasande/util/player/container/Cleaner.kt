package su.mandora.tarasande.util.player.container

import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.*
import net.minecraft.registry.Registries
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueRegistry

class Cleaner(owner: Any, isEnabled: () -> Boolean = { true }) {

    private val keepSameMaterial = ValueBoolean(owner, "Keep same material", true, isEnabled = isEnabled)
    private val keepSameEnchantments = ValueBoolean(owner, "Keep same enchantments", true, isEnabled = isEnabled)
    private val considerDurability = ValueBoolean(owner, "Consider durability", false, isEnabled = isEnabled)
    private val scaleByDurability = ValueBoolean(owner, "Scale by durability", false, isEnabled = isEnabled) // This one is usually pretty bad in my test, keep it because it's a good idea

    private val unwantedItems = object : ValueRegistry<Item>(owner, "Unwanted items", Registries.ITEM, true) {
        override fun filter(key: Item) = key != Items.AIR
        override fun getTranslationKey(key: Any?) = (key as Item).translationKey
    }

    private fun wrapMaterialScore(stack: ItemStack): Float {
        return when (stack.item) {
            is SwordItem -> (stack.item as SwordItem).material.attackDamage
            is ToolItem -> (stack.item as ToolItem).material.durability.toFloat()
            is ArmorItem -> (stack.item as ArmorItem).protection.toFloat()
            else -> 0F
        }.times(if (scaleByDurability.value) (1F - stack.damage / stack.maxDamage.toFloat()) else 1F)
    }

    private fun isSameItemType(stack: ItemStack, otherStack: ItemStack): Boolean {
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

    fun hasBetterEquivalent(stack: ItemStack, list: List<ItemStack>): Boolean {
        if (unwantedItems.isSelected(stack.item))
            return true // There is always something better if even the user doesn't like it
        if (stack.item.enchantability == 0)
            return false // Items without enchanting are usually all equal, let them be...

        val materialScore = wrapMaterialScore(stack)

        val enchantments = EnchantmentHelper.get(stack)

        for (otherStack in list) {
            if (!isSameItemType(stack, otherStack))
                continue

            if (stack.isOf(Items.TURTLE_HELMET) != otherStack.isOf(Items.TURTLE_HELMET))
                continue // Turtle helmets grant water breathing, this is something unique to the turtle helmet, this behaviour is hardcoded into the game

            if (considerDurability.value)
                if (otherStack.damage > stack.damage)
                    continue // If the stack has more damage, then it's worse


            val otherMaterialScore = wrapMaterialScore(otherStack)

            // If the other item has a better material, we need to investigate further
            val worseMaterial = if (keepSameMaterial.value) otherMaterialScore > materialScore else otherMaterialScore >= materialScore

            if (worseMaterial) {
                // There is an item with better material, do we have special enchantments maybe?
                val otherEnchantments = EnchantmentHelper.get(otherStack)
                val missesEnchantment = enchantments.isEmpty() && otherEnchantments.isNotEmpty()
                if (missesEnchantment)
                    return true // Well that was fast ._.
                val worseEnchantments = enchantments.all {
                    if (!otherEnchantments.containsKey(it.key))
                        return@all false // One enchantment wasn't there, means that the item might have value
                    val betterEnchantment = if (keepSameEnchantments.value) otherEnchantments[it.key]!! > it.value else otherEnchantments[it.key]!! >= it.value
                    betterEnchantment // If the enchantment better?
                }
                if (worseEnchantments) // If all enchantments are worse, then we can return true
                    return true

            }
        }

        return false
    }


}