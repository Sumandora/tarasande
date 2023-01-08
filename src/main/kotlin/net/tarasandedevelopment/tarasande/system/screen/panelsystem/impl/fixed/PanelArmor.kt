package net.tarasandedevelopment.tarasande.system.screen.panelsystem.impl.fixed

import net.minecraft.client.util.math.MatrixStack
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.Panel
import net.tarasandedevelopment.tarasande.util.extension.mc
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper

class PanelArmor : Panel("Armor", 75.0, FontWrapper.fontHeight().toDouble()) {

    private val itemDimension = 20

    private val allocateSpaceForEmptySlots = ValueBoolean(this, "Allocate space for empty slots", false)
    private val showEnchantments = ValueBoolean(this, "Enchantments", true)
    private val maxLength = ValueNumber(this, "Max length", 1.0, 3.0, 5.0, 1.0)
    private val scale = ValueNumber(this, "Scale", 0.1, 0.5, 2.0, 0.1)

    override fun isVisible(): Boolean {
        return mc.player?.inventory?.armor?.any { it != null && !it.isEmpty } == true
    }

    private fun enchantSimpleName(enchantment: Enchantment, length: Int) = enchantment.getName(0).string.substring(0, length)

    override fun renderContent(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        var m = 0
        for (i in mc.player!!.inventory.armor.size - 1 downTo 0) {
            val armor = mc.player!!.inventory.armor[i]
            if (armor.isEmpty && allocateSpaceForEmptySlots.value)
                continue

            matrices.push()
            matrices.translate(x, y, 0.0)

            RenderUtil.renderCorrectItem(matrices, m, titleBarHeight, delta, armor)

            if (showEnchantments.value) {
                EnchantmentHelper.get(armor).onEachIndexed { index, entry ->
                    FontWrapper.textShadow(
                        matrices,
                        enchantSimpleName(entry.key, maxLength.value.toInt()) + " " + entry.value,
                        m.toFloat() + itemDimension / 2, (titleBarHeight / 2) + (itemDimension + (index * titleBarHeight / 2)).toFloat(),
                        scale = scale.value.toFloat(),
                        centered = true
                    )
                }
            }
            m += itemDimension

            matrices.pop()
        }
    }
}
