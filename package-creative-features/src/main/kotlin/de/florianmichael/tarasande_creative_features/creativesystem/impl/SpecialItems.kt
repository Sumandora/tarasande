package de.florianmichael.tarasande_creative_features.creativesystem.impl

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import de.florianmichael.tarasande_creative_features.creativesystem.ExploitCreativeSingle
import de.florianmichael.tarasande_creative_features.creativesystem.valuecomponent.meta.ValueButtonItem

class ExploitCreativeSpecialVanillaItems(parent: Any) : ExploitCreativeSingle(parent, "Special Vanilla Items", ItemStack(Items.COMMAND_BLOCK)) {

    inner class SpecialVanillaItems {
        init {
            val items = mutableListOf<Item>(
                Items.COMMAND_BLOCK,
                Items.CHAIN_COMMAND_BLOCK,
                Items.REPEATING_COMMAND_BLOCK,

                Items.COMMAND_BLOCK_MINECART,

                Items.JIGSAW,
                Items.STRUCTURE_BLOCK,
                Items.STRUCTURE_VOID,

                Items.BARRIER,
                Items.PETRIFIED_OAK_SLAB,
                Items.DEBUG_STICK,
                Items.DRAGON_EGG,
                Items.FILLED_MAP,

                Items.WRITTEN_BOOK,
                Items.ENCHANTED_BOOK,
                Items.KNOWLEDGE_BOOK,

                Items.SUSPICIOUS_STEW,
                Items.POTION,
                Items.SPLASH_POTION,
                Items.LINGERING_POTION,
                Items.TIPPED_ARROW
            )

            for (item in items) {
                object : ValueButtonItem(this, StringUtil.uncoverTranslation(item.translationKey), item.defaultStack) {
                    override fun onClick() {
                        give(item.defaultStack)
                    }
                }
            }
        }
    }

    private val specialVanillaItems = SpecialVanillaItems()

    override fun onPress() {
        mc.setScreen(ScreenBetterOwnerValues(this.name, mc.currentScreen!!, specialVanillaItems))
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
