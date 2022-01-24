package su.mandora.tarasande.screen.menu.panel.impl.fixed.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.screen.menu.panel.impl.fixed.PanelFixed

private const val text = "タラサンデ"

class PanelFixedWatermark(x: Double, y: Double) : PanelFixed("Watermark", x, y, MinecraftClient.getInstance().textRenderer.getWidth(text) * 5.0, MinecraftClient.getInstance().textRenderer.fontHeight * 5.0, true, true) {

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val xScale = panelWidth / MinecraftClient.getInstance().textRenderer.getWidth(text)
        val yScale = panelHeight / MinecraftClient.getInstance().textRenderer.fontHeight
        matrices?.push()
        matrices?.translate(x + panelWidth / 2, y + panelHeight / 2, 0.0)
        matrices?.scale(xScale.toFloat(), yScale.toFloat(), 1.0f)
        matrices?.translate(-(x + panelWidth / 2), -(y + panelHeight / 2), 0.0)

        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, text, (x + panelWidth / 2 - MinecraftClient.getInstance().textRenderer.getWidth(text) / 2).toFloat(), (y + panelHeight / 2 - MinecraftClient.getInstance().textRenderer.fontHeight / 2 + 3 / yScale).toFloat(), TarasandeMain.get().clientValues?.accentColor?.getColor()?.rgb!!)
        matrices?.pop()
    }
}