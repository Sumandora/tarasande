package net.tarasandedevelopment.tarasande.system.screen.panelsystem.impl.fixed

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.ItemStack
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.Panel
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper

class PanelArmor : Panel("Armor", 75.0, FontWrapper.fontHeight().toDouble()) {

    private val itemDimension = 20

    private val skipEmpty = ValueBoolean(this, "Skip empty", false)
    private val showEnchantments = ValueBoolean(this, "Enchantments", true)
    private val maxEnchantmentLength = ValueNumber(this, "Enchantments max length", 1.0, 3.0, 5.0, 1.0)
    private val enchantmentScale = ValueNumber(this, "Enchantments scale", 0.1, 0.5, 2.0, 0.1)

    override fun isVisible(): Boolean {
        for (itemStack in MinecraftClient.getInstance().player?.inventory?.armor ?: return false)
            if (itemStack != ItemStack.EMPTY && itemStack != null) return true

        return false
    }

    private fun enchantSimpleName(enchantment: Enchantment, length: Int) = enchantment.getName(0).string.substring(0, length)

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        var m = 0
        for (i in MinecraftClient.getInstance().player!!.inventory.armor.size - 1 downTo 0) {
            val armor = MinecraftClient.getInstance().player!!.inventory.armor[i]
            if (armor == ItemStack.EMPTY && skipEmpty.value)
                continue

            matrices?.push()
            matrices?.translate(x, y, 0.0)

            RenderUtil.renderCorrectItem(matrices!!, m, titleBarHeight, delta, armor)

            if (showEnchantments.value) {
                EnchantmentHelper.get(armor).onEachIndexed { index, entry ->
                    FontWrapper.textShadow(matrices,
                        enchantSimpleName(entry.key, maxEnchantmentLength.value.toInt()) + " " + entry.value,
                        m.toFloat() + itemDimension / 2, (titleBarHeight / 2) + (itemDimension + (index * titleBarHeight / 2)).toFloat(),
                        scale = enchantmentScale.value.toFloat(), centered = true)
                }
            }
            m += itemDimension

            matrices.pop()
        }
    }
}
