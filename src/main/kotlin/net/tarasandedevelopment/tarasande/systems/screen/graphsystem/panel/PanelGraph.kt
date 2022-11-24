package net.tarasandedevelopment.tarasande.systems.screen.graphsystem.panel

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.Graph
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.Panel
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import kotlin.math.max

class PanelGraph(private val graph: Graph) : Panel(graph.name, max(100.0, FontWrapper.getWidth(graph.name) + 10.0), 50.0, fixed = true) {

    init {
        TarasandeMain.managerValue().getValues(graph).forEach {
            it.owner = this
        }
    }

    override fun isVisible(): Boolean {
        return !graph.isEmpty()
    }

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        if(graph.isEmpty()) return
        val values = graph.values()

        val matrix = matrices?.peek()?.positionMatrix!!

        val min = getMin(values)
        val cur = values.last()
        val max = getMax(values)

        val minStr = graph.format(min) ?: return
        val curStr = graph.format(cur) ?: return
        val maxStr = graph.format(max) ?: return

        val minWidth = FontWrapper.getWidth(minStr) * 0.5
        val curWidth = FontWrapper.getWidth(curStr) * 0.5
        val maxWidth = FontWrapper.getWidth(maxStr) * 0.5

        val width = maxOf(minWidth, curWidth, maxWidth) + 1.0

        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.disableCull()
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)

        val onePixel = 1 / MinecraftClient.getInstance().window.scaleFactor

        if(values.size == 1) {
            bufferBuilder.vertex(matrix, x.toFloat(), (y + panelHeight - onePixel - (panelHeight - titleBarHeight - onePixel) * 0.5).toFloat(), 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next()
            bufferBuilder.vertex(matrix, (x + panelWidth - width).toFloat(), (y + panelHeight - onePixel - (panelHeight - titleBarHeight - onePixel) * 0.5).toFloat(), 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next()
        } else {
            for ((index, value) in graph.values().withIndex()) {
                bufferBuilder.vertex(matrix, (x + (panelWidth - width) * (index / graph.bufferLength.toFloat())).toFloat(), (y + panelHeight - onePixel - (panelHeight - titleBarHeight - (1 / MinecraftClient.getInstance().window.scaleFactor)) * normalize(value.toDouble(), min, max)).toFloat(), 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next()
            }
        }
        BufferRenderer.drawWithShader(bufferBuilder.end())
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
        RenderSystem.enableCull()

        val normalizedHeight = 1.0 - this.normalize(cur.toDouble(), min, max)
        val height = (panelHeight - titleBarHeight) * normalizedHeight
        val currentY = y + titleBarHeight + height - FontWrapper.fontHeight() / 2.0 * normalizedHeight

        FontWrapper.textShadow(matrices, curStr, (x + panelWidth - curWidth).toFloat(), currentY.toFloat(), -1, scale = 0.5F, offset = 0.5F)
        if (currentY < y + panelHeight - FontWrapper.fontHeight()) {
            FontWrapper.textShadow(matrices, minStr, (x + panelWidth - minWidth).toFloat(), (y + panelHeight - FontWrapper.fontHeight() / 2).toFloat(), -1, scale = 0.5F, offset = 0.5F)
        }
        if (currentY >= y + FontWrapper.fontHeight() * 1.5) {
            FontWrapper.textShadow(matrices, maxStr, (x + panelWidth - maxWidth).toFloat(), (y + FontWrapper.fontHeight()).toFloat(), -1, scale = 0.5F, offset = 0.5F)
        }
    }

    private fun getMin(values: Array<Number>) = values.minOf { it.toDouble() }

    private fun getMax(values: Array<Number>) = values.maxOf { it.toDouble() }

    private fun normalize(value: Double, min: Double, max: Double): Double {
        return if (min == max) 0.5 else (value - min) / (max - min)
    }
}