package su.mandora.tarasande.util.player.container

import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.*
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.util.math.Vec2f
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.grabbersystem.ManagerGrabber
import su.mandora.tarasande.system.base.grabbersystem.impl.GrabberOffHandSlot
import su.mandora.tarasande.system.base.grabbersystem.impl.GrabberSlotRenderSize
import su.mandora.tarasande.util.extension.minecraft.safeCount

object ContainerUtil {

    @Suppress("UNCHECKED_CAST")
    val offHandIndex = ManagerGrabber.getConstant(GrabberOffHandSlot::class.java) as Pair<Int /* Client ID */, Int /* Networked ID */>

    private val slotWidthAndHeight = ManagerGrabber.getConstant(GrabberSlotRenderSize::class.java) as Int

    fun getDisplayPosition(original: HandledScreen<*>, slot: Slot): Vec2f {
        val halfSize = slotWidthAndHeight / 2
        return Vec2f(original.x + slot.x.toFloat() + halfSize, original.y + slot.y.toFloat() + halfSize)
    }

    fun getValidSlots(screenHandler: ScreenHandler): List<Slot> {
        return screenHandler.slots
            .filter { it != null && it.isEnabled }
    }

    fun getEquipmentSlot(screenHandler: ScreenHandler, equipmentSlot: EquipmentSlot): Slot? {
        return getValidSlots(screenHandler)
            .filter { it.id in 5..8 }
            .firstOrNull { it.stack.item is ArmorItem && (it.stack.item as ArmorItem).slotType == equipmentSlot }
    }

    fun getClosestSlot(screenHandler: ScreenHandler, original: HandledScreen<*>, lastMouseClick: Vec2f, block: (Slot, List<Slot>) -> Boolean): Slot? {
        val list = getValidSlots(screenHandler)
        return list
            .filter { block(it, list) }
            .minByOrNull { lastMouseClick.distanceSquared(getDisplayPosition(original, it)) }
    }

    private fun wrapMaterialScore(stack: ItemStack, durability: Boolean): Float {
        return when (stack.item) {
            is SwordItem -> (stack.item as SwordItem).material.attackDamage
            is ToolItem -> (stack.item as ToolItem).material.durability.toFloat()
            is ArmorItem -> (stack.item as ArmorItem).protection.toFloat()
            else -> 0.0F
        }.times(if (durability) (1.0F - stack.damage / stack.maxDamage.toFloat()) else 1.0F)
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

    fun hasBetterEquivalent(stack: ItemStack, list: List<ItemStack>, keepSameMaterial: Boolean, keepSameEnchantments: Boolean): Boolean {
        if (stack.item.enchantability == 0) // actually a pretty smart check even tho it's not perfect
            return false

        val materialScore = wrapMaterialScore(stack, false)

        val enchantments = EnchantmentHelper.get(stack)

        for (otherStack in list) {
            if (isSameItemType(stack, otherStack)) {
                if(stack.isOf(Items.TURTLE_HELMET) != otherStack.isOf(Items.TURTLE_HELMET))
                    continue // Turtle helmets grant water breathing, this is something unique to the turtle helmet, this behaviour is hardcoded into the game, im going to commit arson

                val otherEnchantments = EnchantmentHelper.get(otherStack)

                val otherMaterialScore = wrapMaterialScore(otherStack, false)

                if ((if (keepSameMaterial) otherMaterialScore > materialScore else otherMaterialScore >= materialScore) && ((enchantments.isEmpty() && otherEnchantments.isNotEmpty()) || enchantments.all { otherEnchantments.containsKey(it.key) && (if (keepSameEnchantments) otherEnchantments[it.key]!! > it.value else otherEnchantments[it.key]!! >= it.value) })) {
//                    if (otherStack.damage > stack.damage * durability)
//                        continue

                    //println("$stack $otherStack $materialScore > $otherMaterialScore ${1.0F - stack.damage / stack.maxDamage.toFloat()} ${1.0F - otherStack.damage / otherStack.maxDamage.toFloat()} ${enchantments.isEmpty() && otherEnchantments.isNotEmpty()} ${((materialScore == null || otherMaterialScore == null) || (if (keepSameMaterial) otherMaterialScore > materialScore else otherMaterialScore >= materialScore))}")
                    return true
                }
            }
        }

        return false
    }

    fun wrapMaterialDamage(stack: ItemStack): Float {
        return when (stack.item) {
            is SwordItem -> (stack.item as SwordItem).material.attackDamage
            is ToolItem -> (stack.item as ToolItem).material.attackDamage
            else -> 0.0F
        }
    }

    fun getHotbarSlots() = mc.player?.inventory?.main?.subList(0, PlayerInventory.getHotbarSize())!!

    fun isInHotbar(index: Int): Boolean {
        return index in (offHandIndex.second - 1).let { it - PlayerInventory.getHotbarSize() .. it }
    }

    fun findSlot(filter: (IndexedValue<ItemStack>) -> Boolean): Int? {
        return getHotbarSlots().withIndex().filter { filter(it) }.minByOrNull { it.value.safeCount() }?.index
    }

    fun getProperEnchantments(stack: ItemStack): Map<Enchantment, Int> {
        return EnchantmentHelper.get(stack).filter { it.key.isAcceptableItem(stack) }
    }
}