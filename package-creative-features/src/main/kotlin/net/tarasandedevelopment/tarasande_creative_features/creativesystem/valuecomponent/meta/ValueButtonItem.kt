package net.tarasandedevelopment.tarasande_creative_features.creativesystem.valuecomponent.meta

import net.minecraft.item.ItemStack
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.ValueButton
import net.tarasandedevelopment.tarasande_creative_features.creativesystem.valuecomponent.ElementWidthValueComponentButtonItem

open class ValueButtonItem(owner: Any, name: String, val icon: ItemStack, manage: Boolean = true) : ValueButton(owner, name, manage) {

    init {
        valueComponent = ElementWidthValueComponentButtonItem::class.java
    }
}
