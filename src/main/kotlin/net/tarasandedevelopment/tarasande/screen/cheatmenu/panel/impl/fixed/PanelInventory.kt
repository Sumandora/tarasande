package net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.fixed

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.Panel
import kotlin.math.floor

class PanelInventory(x: Double, y: Double, screenCheatMenu: ScreenCheatMenu) : Panel("Inventory", x, y, 150.0, 66.0, resizable = false, fixed = true) {

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        var m = 1

        for (q in 9..35) {
            val r = (x + (q % 9) * (panelWidth / 9.0)).toInt()
            val s = (y + titleBarHeight.toDouble() + 2.0 + (floor(q / 9.0) - 1) * ((panelHeight - titleBarHeight) / 3)).toInt()
            RenderSystem.enableCull()
            MinecraftClient.getInstance().inGameHud.renderHotbarItem(r, s, MinecraftClient.getInstance().tickDelta, MinecraftClient.getInstance().player, MinecraftClient.getInstance().player?.inventory?.main?.get(q) as ItemStack, m++)
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
