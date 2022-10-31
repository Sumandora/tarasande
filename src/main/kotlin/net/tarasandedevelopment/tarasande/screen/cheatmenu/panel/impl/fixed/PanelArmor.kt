package net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.fixed

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.ItemStack
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.Panel
import net.tarasandedevelopment.tarasande.util.player.items.ItemUtil
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueNumber

class PanelArmor(x: Double, y: Double, screenCheatMenu: ScreenCheatMenu) : Panel("Armor", x, y, 75.0, MinecraftClient.getInstance().textRenderer.fontHeight.toDouble(), background = false, resizable = false, fixed = true) {

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

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        var m = 0
        for (i in MinecraftClient.getInstance().player!!.inventory.armor.size - 1 downTo 0) {
            val armor = MinecraftClient.getInstance().player!!.inventory.armor[i]
            if (armor == ItemStack.EMPTY && skipEmpty.value)
                    continue

            matrices?.push()
            matrices?.translate(x, y, 0.0)

            RenderSystem.enableCull()
            RenderUtil.renderCorrectItem(matrices!!, m, titleBarHeight, delta, armor)

            if (showEnchantments.value) {
                EnchantmentHelper.get(armor).onEachIndexed { index, entry ->
                    RenderUtil.font().textShadow(matrices,
                        ItemUtil.enchantSimpleName(entry.key, maxEnchantmentLength.value.toInt()) + " " + entry.value,
                        m.toFloat() + itemDimension / 2, (titleBarHeight / 2) + (itemDimension + (index * titleBarHeight / 2)).toFloat(),
                        scale = enchantmentScale.value.toFloat(), centered = true)
                }
            }
            m += itemDimension

            matrices.pop()
        }
    }
}
