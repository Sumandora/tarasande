package su.mandora.tarasande_creative_features.creativesystem.valuecomponent.meta

import su.mandora.tarasande_creative_features.creativesystem.valuecomponent.ElementWidthValueComponentButtonItem
import net.minecraft.item.ItemStack
import su.mandora.tarasande.system.base.valuesystem.impl.meta.ValueButton

open class ValueButtonItem(
    owner: Any,
    name: String,
    val icon: ItemStack,
    visible: Boolean = true,
    isEnabled: () -> Boolean = { true },
    manage: Boolean = true
) : ValueButton(owner, name, visible, isEnabled, manage) {

    init {
        valueComponent = ElementWidthValueComponentButtonItem::class.java
    }
}
