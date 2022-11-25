package net.tarasandedevelopment.tarasande.system.screen.panelsystem.api

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.Panel
import net.tarasandedevelopment.tarasande.util.render.RenderUtil

class ClickableWidgetPanelSidebar(panel: Panel) : ClickableWidgetPanel(panel, true) {

    private var animation = 0.1

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        this.x = (MinecraftClient.getInstance().window.scaledWidth - (this.panel.panelWidth * animation)).toInt()
        this.y = 0
        this.height = MinecraftClient.getInstance().window.scaledHeight

        if(RenderUtil.isHovered(mouseX.toDouble(), mouseY.toDouble(), x.toDouble(), y.toDouble(), (x + width).toDouble(), (y + height).toDouble()))
            animation += 0.005 * RenderUtil.deltaTime
        else
            animation -= 0.005 * RenderUtil.deltaTime
        animation = MathHelper.clamp(animation, 0.1, 1.0)

        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(mouseX < MinecraftClient.getInstance().window.scaledWidth - (this.panel.panelWidth * animation))
            return false
        return super.mouseClicked(mouseX, mouseY, button)
    }

}