package net.tarasandedevelopment.tarasande_creative_features.creativesystem.impl

import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import net.tarasandedevelopment.tarasande_creative_features.creativesystem.ExploitCreative
import net.tarasandedevelopment.tarasande_creative_features.creativesystem.ExploitCreativeSingle
import net.tarasandedevelopment.tarasande_creative_features.creativesystem.valuecomponent.meta.ValueButtonItem
import net.tarasandedevelopment.tarasande_creative_features.util.ItemUtil

class ExploitCreativeSpecialVanillaItems(parent: Any) : ExploitCreativeSingle(parent, "Special Vanilla Items", ItemStack(Items.COMMAND_BLOCK)) {

    override fun onPress() {
    }
}

class ExploitCreativeLightItems(parent: Any) : ExploitCreativeSingle(parent, "Light Levels", ItemStack(Items.LIGHT)) {

    object LightLevels {
        init {
            for (i in 0 until 16) {
                val stack = createLight(i)

                object : ValueButtonItem(this, "Get with Level $i", stack) {
                    override fun onClick() {
                        ItemUtil.give(createLight(i))
                    }
                }
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

    init {
        LightLevels // force-load
    }

    override fun onPress() {
        mc.setScreen(ScreenBetterOwnerValues(this.name, mc.currentScreen!!, LightLevels))
    }
}
