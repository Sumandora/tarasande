package su.mandora.tarasande.system.screen.graphsystem.panel

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.*
import net.minecraft.util.math.MathHelper
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.graphsystem.Graph
import su.mandora.tarasande.system.screen.panelsystem.Panel
import su.mandora.tarasande.util.render.font.FontWrapper
import kotlin.math.max

class PanelGraph(private val graph: Graph) : Panel(graph.name, max(100, FontWrapper.getWidth(graph.name)) + 10.0, 50.0, true) {

    override fun isVisible(): Boolean {
        return graph.values().size > 1
    }

    override fun renderContent(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (graph.isEmpty()) return
        val values = graph.values()

        val matrix = context.matrices.peek()?.positionMatrix!!

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

        val graphBeginX = x
        val graphEndX = x + panelWidth - width

        val onePixel = 1 / mc.window.scaleFactor

        val graphBeginY = y + titleBarHeight + onePixel
        val graphEndY = y + panelHeight - onePixel

        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.disableCull()
        RenderSystem.enableBlend()

        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)

        for ((index, value) in values.withIndex()) {
            val xPercent = index / values.size.toDouble()
            val yPercent = normalize(value.toDouble(), min, max)
            bufferBuilder.vertex(matrix, MathHelper.lerp(xPercent, graphBeginX, graphEndX).toFloat(), MathHelper.lerp(yPercent, graphEndY, graphBeginY).toFloat(), 0F).color(1F, 1F, 1F, 1F).next()
        }
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())

        RenderSystem.disableBlend()
        RenderSystem.enableCull()

        val percent = 1.0 - normalize(cur.toDouble(), min, max)
        val currentY = MathHelper.lerp(percent, graphBeginY, graphEndY) - FontWrapper.fontHeight() / 2.0 * percent

        FontWrapper.textShadow(context, curStr, (x + panelWidth - curWidth).toFloat(), currentY.toFloat(), -1, scale = 0.5F, offset = 0.5F)
        if (currentY > graphBeginY + FontWrapper.fontHeight() * 0.5F) {
            FontWrapper.textShadow(context, maxStr, (x + panelWidth - maxWidth).toFloat(), (y + titleBarHeight).toFloat(), -1, scale = 0.5F, offset = 0.5F)
        }
        if (currentY < graphEndY - FontWrapper.fontHeight()) {
            FontWrapper.textShadow(context, minStr, (x + panelWidth - minWidth).toFloat(), (y + panelHeight - FontWrapper.fontHeight() * 0.5F).toFloat(), -1, scale = 0.5F, offset = 0.5F)
        }
    }

    private fun getMin(values: Array<Number>) = values.minOf { it.toDouble() }

    private fun getMax(values: Array<Number>) = values.maxOf { it.toDouble() }

    private fun normalize(value: Double, min: Double, max: Double): Double {
        return if (min == max) 0.5 else (value - min) / (max - min)
    }

    override fun getValueOwner(): Any {
        return graph
    }
}