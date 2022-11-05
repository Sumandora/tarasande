package net.tarasandedevelopment.tarasande.features.protocol.util

import com.viaversion.viaversion.protocols.protocol1_9to1_8.ArmorType
import net.minecraft.client.MinecraftClient
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.util.registry.Registry

object ArmorUpdater1_8_0 {

    // this is a list of all 1.9 armor items with their id
    private val ids = HashMap<Item, Int>()

    private var oldArmor = 0

    init {
        //@formatter:off
        arrayOf(
            Items.LEATHER_HELMET,       Items.CHAINMAIL_HELMET,         Items.IRON_HELMET,          Items.DIAMOND_HELMET,       Items.GOLDEN_HELMET,
            Items.LEATHER_CHESTPLATE,   Items.CHAINMAIL_CHESTPLATE,     Items.IRON_CHESTPLATE,      Items.DIAMOND_CHESTPLATE,   Items.GOLDEN_CHESTPLATE,
            Items.LEATHER_LEGGINGS,     Items.CHAINMAIL_LEGGINGS,       Items.IRON_LEGGINGS,        Items.DIAMOND_LEGGINGS,     Items.GOLDEN_LEGGINGS,
            Items.LEATHER_BOOTS,        Items.CHAINMAIL_BOOTS,          Items.IRON_BOOTS,           Items.DIAMOND_BOOTS,        Items.GOLDEN_BOOTS
        ).forEach {
            ids[it] = ArmorType.findByType(Registry.ITEM.getId(it).toString()).armorPoints
        }
        //@formatter:on
    }

    fun armor(): Int {
        return MinecraftClient.getInstance().player!!.inventory.armor.sumOf { ids[it.item] ?: 0 }
    }
}
