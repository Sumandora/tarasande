package net.tarasandedevelopment.tarasande.screen.menu.creative

import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.registry.Registry
import net.tarasandedevelopment.tarasande.base.screen.menu.creative.Action
import net.tarasandedevelopment.tarasande.base.screen.menu.creative.ExploitCreative
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

class LightItems : ExploitCreative("Light Levels", ItemStack(Items.LIGHT)) {

    init {
        for (i in 0 until 16) {
            val stack = createLight(i)

            this.createAction("Get with Level $i", stack, object : Action {
                override fun on() {
                    ItemUtil.give(createLight(i))
                }
            })
        }
    }

    private fun createLight(level: Int): ItemStack {
        val stack = ItemStack(Items.LIGHT)
        val base = NbtCompound()

        val blockStateTag = NbtCompound()
        blockStateTag.putInt("level", level)

        base.put("BlockStateTag", blockStateTag)
        stack.nbt = base

        return stack
    }
}