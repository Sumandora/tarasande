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

    private val values = ArrayList<Number>()

    private var ints = false
    private var current = 0.0
    private var min = 0.0
    private var max = 0.0

    private var graphMinWidth = 0.0
    private var graphMaxWidth = 0.0
    private var currentWidth = 0.0
    private var finalWidth = 0.0

    init {
        TarasandeMain.managerValue.getValues(graph).forEach {
            it.owner = this
        }
    }

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
        RenderSystem.enableCull()

        val normalizedHeight = 1.0 - this.normalize(current, min, max)
        val height = (panelHeight - titleBarHeight) * normalizedHeight
        val currentY = y + titleBarHeight + height - FontWrapper.fontHeight() / 2.0 * normalizedHeight

        FontWrapper.textShadow(matrices, (if (ints) current.toInt() else current).toString(), (x + panelWidth - currentWidth).toFloat(), currentY.toFloat(), -1, scale = 0.5F, offset = 0.5F)
        if (currentY < y + panelHeight - FontWrapper.fontHeight()) {
            FontWrapper.textShadow(matrices, (if (ints) min.toInt() else min).toString(), (x + panelWidth - graphMinWidth).toFloat(), (y + panelHeight - FontWrapper.fontHeight() / 2).toFloat(), -1, scale = 0.5F, offset = 0.5F)
        }
        if (currentY >= y + FontWrapper.fontHeight() * 1.5) {
            FontWrapper.textShadow(matrices, (if (ints) max.toInt() else max).toString(), (x + panelWidth - graphMaxWidth).toFloat(), (y + FontWrapper.fontHeight()).toFloat(), -1, scale = 0.5F, offset = 0.5F)
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

            graphMinWidth = FontWrapper.getWidth((if (ints) min.toInt() else min).toString()) / 2.0
            graphMaxWidth = FontWrapper.getWidth((if (ints) max.toInt() else max).toString()) / 2.0
            currentWidth = FontWrapper.getWidth((if (ints) current.toInt() else current).toString()) / 2.0
            finalWidth = panelWidth - max(graphMinWidth, max(graphMaxWidth, currentWidth))
        }
    }


}