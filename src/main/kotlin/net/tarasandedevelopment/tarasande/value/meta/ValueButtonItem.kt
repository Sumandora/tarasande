package net.tarasandedevelopment.tarasande.value.meta

import com.google.gson.JsonElement
import net.minecraft.item.ItemStack
import net.tarasandedevelopment.tarasande.base.value.Value

open class ValueButtonItem(owner: Any, name: String, val icon: ItemStack, manage: Boolean = true) : Value(owner, name, manage) {
    override fun save(): JsonElement? = null
    override fun load(jsonElement: JsonElement) {}
}