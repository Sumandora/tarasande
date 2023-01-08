package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.panel

import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.Panel
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.ClickableWidgetPanel
import net.tarasandedevelopment.tarasande.util.extension.mc
import net.tarasandedevelopment.tarasande.util.render.RenderUtil

class ClickableWidgetPanelSidebar(panel: Panel) : ClickableWidgetPanel(panel, true) {
    private var animation = 0.1

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        this.x = (mc.window.scaledWidth - (this.panel.panelWidth * animation)).toInt()
        this.y = 0
        this.height = mc.window.scaledHeight

        if (RenderUtil.isHovered(mouseX.toDouble(), mouseY.toDouble(), (x - 1).toDouble(), y.toDouble(), (x + width).toDouble(), (y + height).toDouble()))
            animation += 0.005 * RenderUtil.deltaTime
        else
            animation -= 0.005 * RenderUtil.deltaTime
        animation = animation.coerceIn(0.1..1.0)

        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (mouseX < mc.window.scaledWidth - (this.panel.panelWidth * animation))
            return false
        return super.mouseClicked(mouseX, mouseY, button)
    }
}
