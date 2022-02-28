package su.mandora.tarasande.util.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Formatting
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import kotlin.math.*

object RenderUtil {

    var deltaTime = 0.0

    fun isHovered(mouseX: Double, mouseY: Double, left: Double, up: Double, right: Double, bottom: Double): Boolean {
        return mouseX > left && mouseY > up && mouseX < right && mouseY < bottom
    }

    fun fill(matrices: MatrixStack?, x1: Double, y1: Double, x2: Double, y2: Double, color: Int) {
        val matrix = matrices?.peek()?.positionMatrix!!
        val f = (color shr 24 and 0xFF) / 255.0f
        val g = (color shr 16 and 0xFF) / 255.0f
        val h = (color shr 8 and 0xFF) / 255.0f
        val k = (color and 0xFF) / 255.0f
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, min(x1, x2).toFloat(), max(y1, y2).toFloat(), 0.0f).color(g, h, k, f).next()
        bufferBuilder.vertex(matrix, max(x1, x2).toFloat(), max(y1, y2).toFloat(), 0.0f).color(g, h, k, f).next()
        bufferBuilder.vertex(matrix, max(x1, x2).toFloat(), min(y1, y2).toFloat(), 0.0f).color(g, h, k, f).next()
        bufferBuilder.vertex(matrix, min(x1, x2).toFloat(), min(y1, y2).toFloat(), 0.0f).color(g, h, k, f).next()
        bufferBuilder.end()
        BufferRenderer.draw(bufferBuilder)
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

    fun roundedFill(matrices: MatrixStack?, x1: Double, y1: Double, x2: Double, y2: Double, round: Double, color: Int) {
        RenderSystem.disableCull()
        RenderSystem.disableDepthTest()
        val matrix = matrices?.peek()?.positionMatrix!!
        val f = (color shr 24 and 0xFF) / 255.0f
        val g = (color shr 16 and 0xFF) / 255.0f
        val h = (color shr 8 and 0xFF) / 255.0f
        val k = (color and 0xFF) / 255.0f
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR)
        var quarterCircle = 0.5f
        while (quarterCircle <= 0.75f) {
            bufferBuilder.vertex(matrix, (x1 + round + round * cos(quarterCircle * PI * 2)).toFloat(), (y1 + round + round * sin(quarterCircle * PI * 2)).toFloat(), 0.0f).color(g, h, k, f).next()
            quarterCircle += 0.025f
        }
        while (quarterCircle <= 1.0f) {
            bufferBuilder.vertex(matrix, (x2 - round + round * cos(quarterCircle * PI * 2)).toFloat(), (y1 + round + round * sin(quarterCircle * PI * 2)).toFloat(), 0.0f).color(g, h, k, f).next()
            quarterCircle += 0.025f
        }
        quarterCircle = 0.0f
        while (quarterCircle <= 0.25f) {
            bufferBuilder.vertex(matrix, (x2 - round + round * cos(quarterCircle * PI * 2)).toFloat(), (y2 - round + round * sin(quarterCircle * PI * 2)).toFloat(), 0.0f).color(g, h, k, f).next()
            quarterCircle += 0.025f
        }
        while (quarterCircle <= 0.5f) {
            bufferBuilder.vertex(matrix, (x1 + round + round * cos(quarterCircle * PI * 2)).toFloat(), (y2 - round + round * Math.sin(quarterCircle * PI * 2)).toFloat(), 0.0f).color(g, h, k, f).next()
            quarterCircle += 0.025f
        }
        bufferBuilder.end()
        BufferRenderer.draw(bufferBuilder)
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

    fun outlinedFill(matrices: MatrixStack?, x1: Double, y1: Double, x2: Double, y2: Double, width: Float, color: Int) {
        glEnable(GL_LINE_SMOOTH)
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST)
        val lineWidth = glGetFloat(GL_LINE_WIDTH)
        glLineWidth(width)
        val matrix = matrices?.peek()?.positionMatrix!!
        val f = (color shr 24 and 0xFF) / 255.0f
        val g = (color shr 16 and 0xFF) / 255.0f
        val h = (color shr 8 and 0xFF) / 255.0f
        val k = (color and 0xFF) / 255.0f
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, min(x1, x2).toFloat(), max(y1, y2).toFloat(), 0.0f).color(g, h, k, f).next()
        bufferBuilder.vertex(matrix, max(x1, x2).toFloat(), max(y1, y2).toFloat(), 0.0f).color(g, h, k, f).next()
        bufferBuilder.vertex(matrix, max(x1, x2).toFloat(), min(y1, y2).toFloat(), 0.0f).color(g, h, k, f).next()
        bufferBuilder.vertex(matrix, min(x1, x2).toFloat(), min(y1, y2).toFloat(), 0.0f).color(g, h, k, f).next()
        bufferBuilder.vertex(matrix, min(x1, x2).toFloat(), max(y1, y2).toFloat(), 0.0f).color(g, h, k, f).next()
        bufferBuilder.end()
        BufferRenderer.draw(bufferBuilder)
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
        glLineWidth(lineWidth)
        glDisable(GL_LINE_SMOOTH)
    }

    fun outlinedHorizontalGradient(matrices: MatrixStack?, x1: Double, y1: Double, x2: Double, y2: Double, width: Float, colorStart: Int, colorEnd: Int) {
        glEnable(GL_LINE_SMOOTH)
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST)
        val lineWidth = glGetFloat(GL_LINE_WIDTH)
        glLineWidth(width)
        val matrix = matrices?.peek()?.positionMatrix!!
        val f = (colorStart shr 24 and 0xFF) / 255.0f
        val g = (colorStart shr 16 and 0xFF) / 255.0f
        val h = (colorStart shr 8 and 0xFF) / 255.0f
        val i = (colorStart and 0xFF) / 255.0f
        val j = (colorEnd shr 24 and 0xFF) / 255.0f
        val k = (colorEnd shr 16 and 0xFF) / 255.0f
        val l = (colorEnd shr 8 and 0xFF) / 255.0f
        val m = (colorEnd and 0xFF) / 255.0f
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, min(x1, x2).toFloat(), max(y1, y2).toFloat(), 0.0f).color(k, l, m, j).next()
        bufferBuilder.vertex(matrix, max(x1, x2).toFloat(), max(y1, y2).toFloat(), 0.0f).color(g, h, i, f).next()
        bufferBuilder.vertex(matrix, max(x1, x2).toFloat(), min(y1, y2).toFloat(), 0.0f).color(g, h, i, f).next()
        bufferBuilder.vertex(matrix, min(x1, x2).toFloat(), min(y1, y2).toFloat(), 0.0f).color(k, l, m, j).next()
        bufferBuilder.vertex(matrix, min(x1, x2).toFloat(), max(y1, y2).toFloat(), 0.0f).color(k, l, m, j).next()
        bufferBuilder.end()
        BufferRenderer.draw(bufferBuilder)
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
        glLineWidth(lineWidth)
        glDisable(GL_LINE_SMOOTH)
    }

    fun fillHorizontalGradient(matrices: MatrixStack?, x1: Double, y1: Double, x2: Double, y2: Double, colorStart: Int, colorEnd: Int) {
        val matrix = matrices?.peek()?.positionMatrix!!
        val f = (colorStart shr 24 and 0xFF) / 255.0f
        val g = (colorStart shr 16 and 0xFF) / 255.0f
        val h = (colorStart shr 8 and 0xFF) / 255.0f
        val i = (colorStart and 0xFF) / 255.0f
        val j = (colorEnd shr 24 and 0xFF) / 255.0f
        val k = (colorEnd shr 16 and 0xFF) / 255.0f
        val l = (colorEnd shr 8 and 0xFF) / 255.0f
        val m = (colorEnd and 0xFF) / 255.0f
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, x1.toFloat(), y1.toFloat(), 0.0f).color(k, l, m, j).next()
        bufferBuilder.vertex(matrix, x1.toFloat(), y2.toFloat(), 0.0f).color(g, h, i, f).next()
        bufferBuilder.vertex(matrix, x2.toFloat(), y2.toFloat(), 0.0f).color(g, h, i, f).next()
        bufferBuilder.vertex(matrix, x2.toFloat(), y1.toFloat(), 0.0f).color(k, l, m, j).next()
        bufferBuilder.end()
        BufferRenderer.draw(bufferBuilder)
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

    fun fillVerticalGradient(matrices: MatrixStack?, x1: Double, y1: Double, x2: Double, y2: Double, colorStart: Int, colorEnd: Int) {
        val matrix = matrices?.peek()?.positionMatrix!!
        val f = (colorStart shr 24 and 0xFF) / 255.0f
        val g = (colorStart shr 16 and 0xFF) / 255.0f
        val h = (colorStart shr 8 and 0xFF) / 255.0f
        val i = (colorStart and 0xFF) / 255.0f
        val j = (colorEnd shr 24 and 0xFF) / 255.0f
        val k = (colorEnd shr 16 and 0xFF) / 255.0f
        val l = (colorEnd shr 8 and 0xFF) / 255.0f
        val m = (colorEnd and 0xFF) / 255.0f
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, x1.toFloat(), y1.toFloat(), 0.0f).color(g, h, i, f).next()
        bufferBuilder.vertex(matrix, x1.toFloat(), y2.toFloat(), 0.0f).color(k, l, m, j).next()
        bufferBuilder.vertex(matrix, x2.toFloat(), y2.toFloat(), 0.0f).color(k, l, m, j).next()
        bufferBuilder.vertex(matrix, x2.toFloat(), y1.toFloat(), 0.0f).color(g, h, i, f).next()
        bufferBuilder.end()
        BufferRenderer.draw(bufferBuilder)
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

    fun outlinedCircle(matrices: MatrixStack?, x: Double, y: Double, radius: Double, width: Float, color: Int) {
        glEnable(GL_LINE_SMOOTH)
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST)
        val lineWidth = glGetFloat(GL_LINE_WIDTH)
        glLineWidth(width)
        val matrix = matrices?.peek()?.positionMatrix!!
        val f = (color shr 24 and 0xFF) / 255.0f
        val g = (color shr 16 and 0xFF) / 255.0f
        val h = (color shr 8 and 0xFF) / 255.0f
        val k = (color and 0xFF) / 255.0f
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)
        var circle = 0.0
        while (circle <= 1.01) {
            bufferBuilder.vertex(matrix, (x - sin(circle * PI * 2) * radius).toFloat(), (y + cos(circle * PI * 2) * radius).toFloat(), 0.0f).color(g, h, k, f).next()
            circle += 0.01
        }
        bufferBuilder.end()
        BufferRenderer.draw(bufferBuilder)
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
        glLineWidth(lineWidth)
        glDisable(GL_LINE_SMOOTH)
    }

    fun fillCircle(matrices: MatrixStack?, x: Double, y: Double, radius: Double, color: Int) {
        RenderSystem.disableCull()
        RenderSystem.disableDepthTest()
        val matrix = matrices?.peek()?.positionMatrix!!
        val f = (color shr 24 and 0xFF) / 255.0f
        val g = (color shr 16 and 0xFF) / 255.0f
        val h = (color shr 8 and 0xFF) / 255.0f
        val k = (color and 0xFF) / 255.0f
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR)
        var circle = 0.0
        while (circle <= 1.01) {
            bufferBuilder.vertex(matrix, (x - sin(circle * PI * 2) * radius).toFloat(), (y + cos(circle * PI * 2) * radius).toFloat(), 0.0f).color(g, h, k, f).next()
            circle += 0.01
        }
        bufferBuilder.end()
        BufferRenderer.draw(bufferBuilder)
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

    fun colorInterpolate(a: Color, b: Color, t: Double): Color {
        val t = t.toFloat() // damn
        return Color((a.red + (b.red - a.red) * t) / 255.0f, (a.green + (b.green - a.green) * t) / 255.0f, (a.blue + (b.blue - a.blue) * t) / 255.0f, (a.alpha + (b.alpha - a.alpha) * t) / 255.0f)
    }

    fun drawWithSmallShadow(matrices: MatrixStack?, text: String, x: Float, y: Float, color: Int) {
        MinecraftClient.getInstance().textRenderer.draw(matrices, Formatting.strip(text), x + 0.5f, y + 0.5f, Color(0, 0, 0, color shr 24 and 0xFF).rgb)
        MinecraftClient.getInstance().textRenderer.draw(matrices, text, x, y, color)
    }

    fun formattingByHex(hex: Int): Formatting {
        var bestFormatting: Formatting? = null
        var bestDiff = 0.0
        val red = (hex shr 16 and 0xFF) / 255.0f
        val green = (hex shr 8 and 0xFF) / 255.0f
        val blue = (hex shr 0 and 0xFF) / 255.0f

        for (formatting in Formatting.values()) {
            if (formatting.colorValue == null) continue

            val otherRed = (formatting.colorValue!! shr 16 and 0xFF) / 255.0f
            val otherGreen = (formatting.colorValue!! shr 8 and 0xFF) / 255.0f
            val otherBlue = (formatting.colorValue!! shr 0 and 0xFF) / 255.0f

            val diff = (otherRed - red).toDouble().pow(2.0) * 0.2126 +
                    (otherGreen - green).toDouble().pow(2.0) * 0.7152 +
                    (otherBlue - blue).toDouble().pow(2.0) * 0.0722

            if (bestFormatting == null || bestDiff > diff) {
                bestFormatting = formatting
                bestDiff = diff
            }
        }
        return bestFormatting!!
    }
}