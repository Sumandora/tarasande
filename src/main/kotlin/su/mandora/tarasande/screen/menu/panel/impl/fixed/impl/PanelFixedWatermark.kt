package su.mandora.tarasande.screen.menu.panel.impl.fixed.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.screen.menu.panel.impl.fixed.PanelFixed

private const val text = "タラサンデ"

class PanelFixedWatermark(x: Double, y: Double) : PanelFixed("Watermark", x, y, MinecraftClient.getInstance().textRenderer.getWidth(text) * 5.0, MinecraftClient.getInstance().textRenderer.fontHeight * 5.0, true, true) {

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        // Mind the shadows and leave some space
        val xScale = (panelWidth - 4) / (MinecraftClient.getInstance().textRenderer.getWidth(text) + 1.0)
        val yScale = (panelHeight - 4) / (MinecraftClient.getInstance().textRenderer.fontHeight + 1.0)

        matrices?.push()
        matrices?.translate(x + 1, y + MinecraftClient.getInstance().textRenderer.fontHeight + 1, 0.0)
        matrices?.scale(xScale.toFloat(), yScale.toFloat(), 1.0f)
        matrices?.translate(-(x + 1), -(y + MinecraftClient.getInstance().textRenderer.fontHeight + 1), 0.0)

        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, text, (x + 1).toFloat(), (y + MinecraftClient.getInstance().textRenderer.fontHeight + 1).toFloat(), TarasandeMain.get().clientValues?.accentColor?.getColor()?.rgb!!)
        matrices?.pop()
    }
}