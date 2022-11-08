package net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.fixed

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.Panel
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import kotlin.math.floor

class PanelInventory(x: Double, y: Double, screenCheatMenu: ScreenCheatMenu) : Panel("Inventory", x, y, 150.0, 66.0, resizable = false, fixed = true) {

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        for (q in 9..35) {
            val r = (x + (q % 9) * (panelWidth / 9.0)).toInt()
            val s = (y + titleBarHeight.toDouble() + 2.0 + (floor(q / 9.0) - 1) * ((panelHeight - titleBarHeight) / 3)).toInt()
            RenderUtil.renderCorrectItem(matrices!!, r, s, MinecraftClient.getInstance().tickDelta, MinecraftClient.getInstance().player?.inventory?.main?.get(q) as ItemStack)
        }
    }

    override fun isVisible(): Boolean {
        for (q in 9..35) {
            val itemStack = MinecraftClient.getInstance().player?.inventory?.main?.get(q)
            if (itemStack != null && !itemStack.isEmpty) return true
        }
        return false
    }
}
