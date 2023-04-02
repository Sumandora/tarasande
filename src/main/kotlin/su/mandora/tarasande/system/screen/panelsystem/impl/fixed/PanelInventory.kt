package su.mandora.tarasande.system.screen.panelsystem.impl.fixed

import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.panelsystem.Panel
import su.mandora.tarasande.util.render.RenderUtil
import kotlin.math.floor

class PanelInventory : Panel("Inventory", 150.0, 66.0, true) {

    override fun renderContent(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        for (q in 9..35) {
            val r = (x + (q % 9) * (panelWidth / 9.0)).toInt()
            val s = (y + titleBarHeight.toDouble() + 2.0 + (floor(q / 9.0) - 1) * ((panelHeight - titleBarHeight) / 3)).toInt()
            RenderUtil.renderItemStack(matrices, r, s, delta, mc.player?.inventory?.main?.get(q) as ItemStack)
        }
    }

    override fun isVisible(): Boolean {
        for (q in 9..35) {
            val itemStack = mc.player?.inventory?.main?.get(q)
            if (itemStack != null && !itemStack.isEmpty) return true
        }
        return false
    }
}
