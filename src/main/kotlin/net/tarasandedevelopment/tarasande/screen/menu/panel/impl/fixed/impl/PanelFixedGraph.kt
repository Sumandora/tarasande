package net.tarasandedevelopment.tarasande.screen.menu.panel.impl.fixed.impl

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.base.screen.menu.graph.Graph
import net.tarasandedevelopment.tarasande.screen.menu.panel.impl.fixed.PanelFixed
import java.awt.Color
import kotlin.math.max

class PanelFixedGraph(private val graph: Graph, x: Double, y: Double) : PanelFixed(graph.name, x, y, max(100.0, MinecraftClient.getInstance().textRenderer.getWidth(graph.name) + 10.0), 50.0, background = true) {

    private val values = ArrayList<Number>()

    private var ints = false
    private var current = 0.0
    private var min = 0.0
    private var max = 0.0

    private var graphMinWidth = 0.0
    private var graphMaxWidth = 0.0
    private var currentWidth = 0.0
    private var finalWidth = 0.0

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        if (values.isEmpty()) {
            return
        }

        val matrix = matrices?.peek()?.positionMatrix!!

        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.disableCull()
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)
        for ((index, value) in values.withIndex()) {
            bufferBuilder.vertex(matrix, (x + finalWidth * (index / values.size.toFloat())).toFloat(), (y + panelHeight - (1 / MinecraftClient.getInstance().window.scaleFactor) - (panelHeight - titleBarHeight - (1 / MinecraftClient.getInstance().window.scaleFactor)) * normalize(value.toDouble(), min, max)).toFloat(), 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next()
        }
        BufferRenderer.drawWithShader(bufferBuilder.end())
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()

        val normalizedHeight = 1.0 - this.normalize(current, min, max)
        val height = (panelHeight - titleBarHeight) * normalizedHeight
        val currentY = y + titleBarHeight + height - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0 * normalizedHeight

        matrices.push()
        matrices.translate(x + panelWidth - currentWidth, currentY, 0.0)
        matrices.scale(0.5f, 0.5f, 1.0f)
        matrices.translate(-(x + panelWidth - currentWidth), -currentY, 0.0)
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, (if (ints) current.toInt() else current).toString(), (x + panelWidth - currentWidth).toFloat(), currentY.toFloat(), Color.white.rgb)
        matrices.pop()
        if (currentY < y + panelHeight - MinecraftClient.getInstance().textRenderer.fontHeight) {
            matrices.push()
            matrices.translate(x + panelWidth - graphMinWidth, y + panelHeight - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0, 0.0)
            matrices.scale(0.5f, 0.5f, 1.0f)
            matrices.translate(-(x + panelWidth - graphMinWidth), -(y + panelHeight - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0), 0.0)
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, (if (ints) min.toInt() else min).toString(), (x + panelWidth - graphMinWidth).toFloat(), (y + panelHeight - MinecraftClient.getInstance().textRenderer.fontHeight / 2).toFloat(), Color.white.rgb)
            matrices.pop()
        }
        if (currentY >= y + MinecraftClient.getInstance().textRenderer.fontHeight * 1.5) {
            matrices.push()
            matrices.translate(x + panelWidth - graphMaxWidth, y + MinecraftClient.getInstance().textRenderer.fontHeight, 0.0)
            matrices.scale(0.5f, 0.5f, 1.0f)
            matrices.translate(-(x + panelWidth - graphMaxWidth), -(y + MinecraftClient.getInstance().textRenderer.fontHeight), 0.0)
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, (if (ints) max.toInt() else max).toString(), (x + panelWidth - graphMaxWidth).toFloat(), (y + MinecraftClient.getInstance().textRenderer.fontHeight).toFloat(), Color.white.rgb)
            matrices.pop()
        }
    }

    private fun getMin(): Number? {
        var num: Number? = null
        for (value in this.values) {
            if (num == null || value.toDouble() <= num.toDouble()) {
                num = value
            }
        }
        return num
    }

    private fun getMax(): Number? {
        var num: Number? = null
        for (value in this.values) {
            if (num == null || value.toDouble() >= num.toDouble()) {
                num = value
            }
        }
        return num
    }

    private fun normalize(value: Double, min: Double, max: Double): Double {
        return if (min == max) 0.5 else (value - min) / (max - min)
    }

    override fun tick() {
        val data: Number? = graph.lastData
        if (data != null) {
            this.values.add(data)
            while (this.values.size > this.graph.bufferLength) {
                this.values.removeAt(0)
            }

            current = values[values.size - 1].toDouble()
            min = getMin()?.toDouble()!!
            max = getMax()?.toDouble()!!

            ints = values.all { it is Int }

            graphMinWidth = MinecraftClient.getInstance().textRenderer.getWidth((if (ints) min.toInt() else min).toString()) / 2.0
            graphMaxWidth = MinecraftClient.getInstance().textRenderer.getWidth((if (ints) max.toInt() else max).toString()) / 2.0
            currentWidth = MinecraftClient.getInstance().textRenderer.getWidth((if (ints) current.toInt() else current).toString()) / 2.0
            finalWidth = panelWidth - max(graphMinWidth, max(graphMaxWidth, currentWidth))
        }
    }


}