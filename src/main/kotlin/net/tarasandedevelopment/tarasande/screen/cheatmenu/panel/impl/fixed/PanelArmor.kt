package net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.fixed

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.tarasandedevelopment.tarasande.mixin.accessor.IInGameHud
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.Panel
import net.tarasandedevelopment.tarasande.util.player.items.ItemUtil
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueNumber
import java.awt.Color

class PanelArmor(x: Double, y: Double, screenCheatMenu: ScreenCheatMenu) : Panel("Armor", x, y, 75.0, MinecraftClient.getInstance().textRenderer.fontHeight.toDouble(), background = false, resizable = false, fixed = true) {

    private val itemDimension = 20

    private val skipEmpty = ValueBoolean(this, "Skip empty", false)
    private val showEnchantments = ValueBoolean(this, "Show enchantments", true)
    private val maxEnchantmentLength = ValueNumber(this, "Max enchantment length", 1.0, 3.0, 5.0, 1.0)

    override fun isVisible(): Boolean {
        for (itemStack in MinecraftClient.getInstance().player!!.inventory.armor)
            if (itemStack != ItemStack.EMPTY && itemStack != null) return true

        return false
    }

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        var m = 0
        for (i in MinecraftClient.getInstance().player!!.inventory.armor.size - 1 downTo 0) {
            val armor = MinecraftClient.getInstance().player!!.inventory.armor[i]
            if (armor == ItemStack.EMPTY && skipEmpty.value)
                    return

            (MinecraftClient.getInstance().inGameHud as IInGameHud).tarasande_invokeRenderHotbarItem(x.toInt() + m, y.toInt() + titleBarHeight, delta, matrices, armor)
            if (showEnchantments.value) {
                val scale = 0.5F

                matrices?.push()
                matrices?.scale(scale, scale, 1F)
                matrices?.translate(x / scale, (y / scale) + (titleBarHeight), 0.0)
                EnchantmentHelper.get(armor).onEachIndexed { index, entry ->
                    RenderUtil.textCenter(
                        matrices,
                        ItemUtil.enchantSimpleName(entry.key, maxEnchantmentLength.value.toInt()) + " " + entry.value,
                        (m.toFloat() + (itemDimension / 2)) / scale,
                        (itemDimension + (index * titleBarHeight / 2)) / scale,
                        Color.white.rgb
                    )
                }
                matrices?.pop()
            }
            m += itemDimension
        }
    }
}