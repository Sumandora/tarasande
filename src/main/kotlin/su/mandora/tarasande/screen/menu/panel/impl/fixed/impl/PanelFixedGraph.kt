package su.mandora.tarasande.screen.menu.panel.impl.fixed.impl

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import su.mandora.tarasande.base.screen.menu.graph.Graph
import su.mandora.tarasande.screen.menu.panel.impl.fixed.PanelFixed
import kotlin.math.max

class PanelFixedGraph(private val graph: Graph, x: Double, y: Double) : PanelFixed(graph.name, x, y, max(100.0, MinecraftClient.getInstance().textRenderer.getWidth(graph.name) + 10.0), 50.0, background = true) {

    private val values = ArrayList<Number>()

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        if (values.isEmpty()) {
            return
        }

        val current = values[values.size - 1].toDouble()
        val min = getMin()?.toDouble()!!
        val max = getMax()?.toDouble()!!

        val minWidth = MinecraftClient.getInstance().textRenderer.getWidth(min.toString()) / 2.0
        val maxWidth = MinecraftClient.getInstance().textRenderer.getWidth(max.toString()) / 2.0
        val currentWidth = MinecraftClient.getInstance().textRenderer.getWidth(current.toString()) / 2.0
        val finalWidth = panelWidth - max(minWidth, max(maxWidth, currentWidth))
        val matrix = matrices?.peek()?.positionMatrix!!

        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.disableCull()
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)
        for((index, value) in values.withIndex()) {
            bufferBuilder.vertex(matrix, (x + finalWidth * (index / values.size.toFloat())).toFloat(), (y + panelHeight - (panelHeight - MinecraftClient.getInstance().textRenderer.fontHeight) * normalize(value.toDouble(), min, max)).toFloat(), 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next()
        }
        bufferBuilder.end()
        BufferRenderer.draw(bufferBuilder)
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()

        val normalizedHeight = 1.0 - this.normalize(current, min, max)
        val height = (panelHeight - MinecraftClient.getInstance().textRenderer.fontHeight) * normalizedHeight
        val currentY = y + MinecraftClient.getInstance().textRenderer.fontHeight + height - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0 * normalizedHeight

        matrices.push()
        matrices.translate(x + panelWidth - currentWidth, currentY, 0.0)
        matrices.scale(0.5f, 0.5f, 1.0f)
        matrices.translate(-(x + panelWidth - currentWidth), -currentY, 0.0)
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, current.toString(), (x + panelWidth - currentWidth).toFloat(), currentY.toFloat(), -1)
        matrices.pop()
        if (currentY < y + panelHeight - MinecraftClient.getInstance().textRenderer.fontHeight) {
            matrices.push()
            matrices.translate(x + panelWidth - minWidth, y + panelHeight - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0, 0.0)
            matrices.scale(0.5f, 0.5f, 1.0f)
            matrices.translate(-(x + panelWidth - minWidth), -(y + panelHeight - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0), 0.0)
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, min.toString(), (x + panelWidth - minWidth).toFloat(), (y + panelHeight - MinecraftClient.getInstance().textRenderer.fontHeight / 2).toFloat(), -1)
            matrices.pop()
        }
        if (currentY >= y + MinecraftClient.getInstance().textRenderer.fontHeight * 1.5) {
            matrices.push()
            matrices.translate(x + panelWidth - maxWidth, y + MinecraftClient.getInstance().textRenderer.fontHeight, 0.0)
            matrices.scale(0.5f, 0.5f, 1.0f)
            matrices.translate(-(x + panelWidth - maxWidth), -(y + MinecraftClient.getInstance().textRenderer.fontHeight), 0.0)
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, max.toString(), (x + panelWidth - maxWidth).toFloat(), (y + MinecraftClient.getInstance().textRenderer.fontHeight).toFloat(), -1)
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
        val data: Number? = graph.supplyData()
        if (data != null) {
            this.values.add(data)
        }
        while (this.values.size > this.graph.bufferLength) {
            this.values.removeAt(0)
        }
    }


}