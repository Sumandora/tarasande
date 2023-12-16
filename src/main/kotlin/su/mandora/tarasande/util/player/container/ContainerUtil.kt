package su.mandora.tarasande.util.player.container

import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.Equipment
import net.minecraft.item.ItemStack
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolItem
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.util.math.Vec2f
import su.mandora.tarasande.mc
import su.mandora.tarasande.util.SLOT_RENDER_SIZE
import su.mandora.tarasande.util.extension.minecraft.safeCount

object ContainerUtil {

    fun getDisplayPosition(slot: Slot): Vec2f {
        val halfSize = SLOT_RENDER_SIZE / 2
        return Vec2f(slot.x.toFloat() + halfSize, slot.y.toFloat() + halfSize)
    }

    fun getValidSlots(screenHandler: ScreenHandler): List<Slot> {
        return screenHandler.slots
            .filter { it != null && it.isEnabled }
    }

    fun getEquipmentSlot(screenHandler: ScreenHandler, equipmentSlot: EquipmentSlot): Slot? {
        return getValidSlots(screenHandler)
            .filter { it.id in 5..8 }
            .firstOrNull { it.stack.item is Equipment && (it.stack.item as Equipment).slotType == equipmentSlot }
    }

    fun getClosestSlot(screenHandler: ScreenHandler, lastMouseClick: Vec2f, block: (Slot, List<Slot>) -> Boolean): Slot? {
        val list = getValidSlots(screenHandler)
        return list
            .filter { block(it, list) }
            .minByOrNull { lastMouseClick.distanceSquared(getDisplayPosition(it)) }
    }

    fun wrapMaterialDamage(stack: ItemStack): Float {
        return when (stack.item) {
            is SwordItem -> (stack.item as SwordItem).material.attackDamage
            is ToolItem -> (stack.item as ToolItem).material.attackDamage
            else -> 0F
        }
    }

    fun getHotbarSlots() = mc.player?.inventory?.main?.subList(0, PlayerInventory.getHotbarSize())!!

    fun isInHotbar(index: Int): Boolean {
        return index in (getOffHandSlot(mc.player?.playerScreenHandler!!).id - 1).let { it - PlayerInventory.getHotbarSize()..it }
    }

    fun findSlot(filter: (IndexedValue<ItemStack>) -> Boolean): Int? {
        return getHotbarSlots().withIndex().filter { filter(it) }.minByOrNull { it.value.safeCount() }?.index
    }

    fun getProperEnchantments(stack: ItemStack): Map<Enchantment, Int> {
        return EnchantmentHelper.get(stack).filter { it.key.isAcceptableItem(stack) }
    }

    fun getOffHandSlot(screenHandler: ScreenHandler): Slot {
        return screenHandler.slots.first { it.backgroundSprite?.second == PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT }
    }
}
