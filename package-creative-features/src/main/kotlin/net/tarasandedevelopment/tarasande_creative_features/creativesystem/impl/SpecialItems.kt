package net.tarasandedevelopment.tarasande_creative_features.creativesystem.impl

import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import net.tarasandedevelopment.tarasande_creative_features.creativesystem.ExploitCreativeSingle
import net.tarasandedevelopment.tarasande_creative_features.creativesystem.valuecomponent.meta.ValueButtonItem

class ExploitCreativeSpecialVanillaItems(parent: Any) : ExploitCreativeSingle(parent, "Special Vanilla Items", ItemStack(Items.COMMAND_BLOCK)) {

    override fun onPress() {
    }
}

class ExploitCreativeLightItems(parent: Any) : ExploitCreativeSingle(parent, "Light Levels", ItemStack(Items.LIGHT)) {

    inner class LightLevels {
        init {
            for (i in 0 until 16) {
                val stack = createLight(i)

                object : ValueButtonItem(this, "Get with Level $i", stack) {
                    override fun onClick() {
                        this@ExploitCreativeLightItems.give(createLight(i))
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

    private val lightLevels = LightLevels()

    override fun onPress() {
        mc.setScreen(ScreenBetterOwnerValues(this.name, mc.currentScreen!!, lightLevels))
    }
}
