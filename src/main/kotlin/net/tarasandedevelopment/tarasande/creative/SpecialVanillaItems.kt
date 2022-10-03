package net.tarasandedevelopment.tarasande.creative

import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.registry.Registry
import net.tarasandedevelopment.tarasande.base.creative.Action
import net.tarasandedevelopment.tarasande.base.creative.ExploitCreative
import net.tarasandedevelopment.tarasande.util.ItemUtil
import net.tarasandedevelopment.tarasande.util.string.StringUtil

class SpecialVanillaItems : ExploitCreative("Special Vanilla Items", ItemStack(Items.COMMAND_BLOCK)) {

    init {
        Registry.ITEM.filter { it.asItem().group == null && it.asItem() != Items.AIR }.forEach {
            this.createAction("Get " + StringUtil.uncoverTranslation(it.translationKey), it.defaultStack, object : Action {
                override fun on() {
                    ItemUtil.give(it.defaultStack)
                }
            })
        }
    }
}
