package net.tarasandedevelopment.tarasande.system.screen.panelsystem.impl.fixed

import net.minecraft.client.util.math.MatrixStack
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.Panel
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import kotlin.math.roundToInt

class PanelArmor : Panel("Armor", 75.0, FontWrapper.fontHeight().toDouble()) {

    private val itemDimension = 20

    private val allocateSpaceForEmptySlots = ValueBoolean(this, "Allocate space for empty slots", false)
    private val showEnchantments = ValueBoolean(this, "Enchantments", true)
    private val maxLength = ValueNumber(this, "Max length", 1.0, 3.0, 5.0, 1.0, isEnabled = { showEnchantments.value })
    private val scale = ValueNumber(this, "Scale", 0.1, 0.5, 2.0, 0.1, isEnabled = { showEnchantments.value })

    override fun isVisible(): Boolean {
        return mc.player?.inventory?.armor?.any { it != null && !it.isEmpty } == true
    }

    private fun getEnchantmentName(enchantment: Enchantment) =
        enchantment.getName(0).string.let {
            if(it.length > maxLength.value)
                it.substring(0, maxLength.value.toInt())
            else
                it
        }

    override fun renderContent(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        var m = 0
        for (i in mc.player!!.inventory.armor.size - 1 downTo 0) {
            val armor = mc.player!!.inventory.armor[i]
            if (armor.isEmpty && allocateSpaceForEmptySlots.value)
                continue

            RenderUtil.renderCorrectItem(matrices, (x + m).roundToInt(), (y + titleBarHeight).roundToInt(), delta, armor)

            if (showEnchantments.value) {
                EnchantmentHelper.get(armor).onEachIndexed { index, entry ->
                    FontWrapper.textShadow(
                        matrices,
                        getEnchantmentName(entry.key) + " " + entry.value,
                        (x + m.toFloat() + itemDimension / 2).toFloat(), (y + (titleBarHeight / 2) + itemDimension + (index * titleBarHeight / 2)).toFloat(),
                        scale = scale.value.toFloat(),
                        offset = 0.5F,
                        centered = true
                    )
                }
            }
            m += itemDimension
        }
    }
}
