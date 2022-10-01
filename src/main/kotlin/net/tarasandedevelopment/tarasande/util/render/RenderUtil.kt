package net.tarasandedevelopment.tarasande.util.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import kotlin.math.*

object RenderUtil {

    var deltaTime = 0.0

    fun isHovered(mouseX: Double, mouseY: Double, left: Double, up: Double, right: Double, bottom: Double): Boolean {
        return mouseX > left && mouseY > up && mouseX < right && mouseY < bottom
    }

    fun fill(matrices: MatrixStack?, x1: Double, y1: Double, x2: Double, y2: Double, color: Int) {
        val matrix = matrices!!.peek().positionMatrix
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
        BufferRenderer.drawWithShader(bufferBuilder.end())
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

    fun roundedFill(matrices: MatrixStack?, x1: Double, y1: Double, x2: Double, y2: Double, round: Double, color: Int) {
        RenderSystem.disableCull()
        RenderSystem.disableDepthTest()
        val matrix = matrices!!.peek().positionMatrix
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
            bufferBuilder.vertex(matrix, (x1 + round + round * cos(quarterCircle * PI * 2)).toFloat(), (y2 - round + round * sin(quarterCircle * PI * 2)).toFloat(), 0.0f).color(g, h, k, f).next()
            quarterCircle += 0.025f
        }
        BufferRenderer.drawWithShader(bufferBuilder.end())
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

    fun outlinedFill(matrices: MatrixStack?, x1: Double, y1: Double, x2: Double, y2: Double, width: Float, color: Int) {
        glEnable(GL_LINE_SMOOTH)
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST)
        val lineWidth = glGetFloat(GL_LINE_WIDTH)
        glLineWidth(width)
        val matrix = matrices!!.peek().positionMatrix
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
        BufferRenderer.drawWithShader(bufferBuilder.end())
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
        val matrix = matrices!!.peek().positionMatrix
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
        BufferRenderer.drawWithShader(bufferBuilder.end())
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
        glLineWidth(lineWidth)
        glDisable(GL_LINE_SMOOTH)
    }

    fun fillHorizontalGradient(matrices: MatrixStack?, x1: Double, y1: Double, x2: Double, y2: Double, colorStart: Int, colorEnd: Int) {
        val matrix = matrices!!.peek().positionMatrix
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
        bufferBuilder.vertex(matrix, x1.toFloat(), y2.toFloat(), 0.0f).color(k, l, m, j).next()
        bufferBuilder.vertex(matrix, x2.toFloat(), y2.toFloat(), 0.0f).color(g, h, i, f).next()
        bufferBuilder.vertex(matrix, x2.toFloat(), y1.toFloat(), 0.0f).color(g, h, i, f).next()
        BufferRenderer.drawWithShader(bufferBuilder.end())
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

    fun fillVerticalGradient(matrices: MatrixStack?, x1: Double, y1: Double, x2: Double, y2: Double, colorStart: Int, colorEnd: Int) {
        val matrix = matrices!!.peek().positionMatrix
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
        BufferRenderer.drawWithShader(bufferBuilder.end())
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

    fun outlinedCircle(matrices: MatrixStack?, x: Double, y: Double, radius: Double, width: Float, color: Int) {
        glEnable(GL_LINE_SMOOTH)
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST)
        val lineWidth = glGetFloat(GL_LINE_WIDTH)
        glLineWidth(width)
        val matrix = matrices!!.peek().positionMatrix
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
        BufferRenderer.drawWithShader(bufferBuilder.end())
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
        glLineWidth(lineWidth)
        glDisable(GL_LINE_SMOOTH)
    }

    fun fillCircle(matrices: MatrixStack?, x: Double, y: Double, radius: Double, color: Int) {
        RenderSystem.disableCull()
        RenderSystem.disableDepthTest()
        val matrix = matrices!!.peek().positionMatrix
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
        BufferRenderer.drawWithShader(bufferBuilder.end())
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

    fun blockOutline(matrices: MatrixStack?, voxelShape: VoxelShape, color: Int) {
        if (voxelShape.isEmpty) return

        val f = (color shr 24 and 0xFF) / 255.0f
        val g = (color shr 16 and 0xFF) / 255.0f
        val h = (color shr 8 and 0xFF) / 255.0f
        val k = (color and 0xFF) / 255.0f

        val matrix = matrices!!.peek().positionMatrix

        RenderSystem.enableBlend()
        RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_LINE_SMOOTH)
        RenderSystem.disableDepthTest()
        RenderSystem.setShaderColor(g, h, k, f)
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.setShader { GameRenderer.getPositionShader() }

        val vec3d = MinecraftClient.getInstance().gameRenderer.camera.pos
        val shape = voxelShape.offset(-vec3d.x, -vec3d.y, -vec3d.z)

        val minX = shape.getMin(Direction.Axis.X).toFloat()
        val maxX = shape.getMax(Direction.Axis.X).toFloat()

        val minY = shape.getMin(Direction.Axis.Y).toFloat()
        val maxY = shape.getMax(Direction.Axis.Y).toFloat()

        val minZ = shape.getMin(Direction.Axis.Z).toFloat()
        val maxZ = shape.getMax(Direction.Axis.Z).toFloat()

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION)
        bufferBuilder.vertex(matrix, minX, minY, minZ).next()
        bufferBuilder.vertex(matrix, maxX, minY, minZ).next()
        bufferBuilder.vertex(matrix, maxX, minY, maxZ).next()
        bufferBuilder.vertex(matrix, minX, minY, maxZ).next()

        bufferBuilder.vertex(matrix, minX, maxY, minZ).next()
        bufferBuilder.vertex(matrix, minX, maxY, maxZ).next()
        bufferBuilder.vertex(matrix, maxX, maxY, maxZ).next()
        bufferBuilder.vertex(matrix, maxX, maxY, minZ).next()

        bufferBuilder.vertex(matrix, minX, minY, minZ).next()
        bufferBuilder.vertex(matrix, minX, maxY, minZ).next()
        bufferBuilder.vertex(matrix, maxX, maxY, minZ).next()
        bufferBuilder.vertex(matrix, maxX, minY, minZ).next()

        bufferBuilder.vertex(matrix, maxX, minY, minZ).next()
        bufferBuilder.vertex(matrix, maxX, maxY, minZ).next()
        bufferBuilder.vertex(matrix, maxX, maxY, maxZ).next()
        bufferBuilder.vertex(matrix, maxX, minY, maxZ).next()

        bufferBuilder.vertex(matrix, minX, minY, maxZ).next()
        bufferBuilder.vertex(matrix, maxX, minY, maxZ).next()
        bufferBuilder.vertex(matrix, maxX, maxY, maxZ).next()
        bufferBuilder.vertex(matrix, minX, maxY, maxZ).next()

        bufferBuilder.vertex(matrix, minX, minY, minZ).next()
        bufferBuilder.vertex(matrix, minX, minY, maxZ).next()
        bufferBuilder.vertex(matrix, minX, maxY, maxZ).next()
        bufferBuilder.vertex(matrix, minX, maxY, minZ).next()

        BufferRenderer.drawWithShader(bufferBuilder.end())

        RenderSystem.enableDepthTest()
        RenderSystem.enableBlend()
        glDisable(GL_LINE_SMOOTH)
    }

    fun colorInterpolate(a: Color, b: Color, t: Double) = colorInterpolate(a, b, t, t, t, t)

    fun colorInterpolate(a: Color, b: Color, tR: Double, tG: Double, tB: Double, tA: Double): Color {
        return Color((a.red + (b.red - a.red) * tR.toFloat()) / 255.0f, (a.green + (b.green - a.green) * tG.toFloat()) / 255.0f, (a.blue + (b.blue - a.blue) * tB.toFloat()) / 255.0f, (a.alpha + (b.alpha - a.alpha) * tA.toFloat()) / 255.0f)
    }

    fun drawWithSmallShadow(matrices: MatrixStack?, text: String, x: Float, y: Float, color: Int) {
        MinecraftClient.getInstance().textRenderer.draw(matrices, Formatting.strip(text), x + 0.5f, y + 0.5f, Color(color, true).darker().darker().darker().darker().rgb)
        MinecraftClient.getInstance().textRenderer.draw(matrices, text, x, y, color)
    }

    fun text(matrices: MatrixStack?, text: String, x: Float, y: Float) = text(matrices, Text.literal(text), x, y)
    fun text(matrices: MatrixStack?, text: Text, x: Float, y: Float) = text(matrices, text, x, y, -1)
    fun text(matrices: MatrixStack?, text: Text, x: Float, y: Float, color: Int): Int {
        return font().drawWithShadow(matrices, text, x, y, color)
    }

    fun textCenter(matrices: MatrixStack?, text: String, x: Float, y: Float) {
        textCenter(matrices, Text.literal(text), x, y)
    }
    fun textCenter(matrices: MatrixStack?, text: Text, x: Float, y: Float) = textCenter(matrices, text, x, y, -1)
    fun textCenter(matrices: MatrixStack?, text: Text, x: Float, y: Float, color: Int) {
        font().drawWithShadow(matrices, text, (x - font().getWidth(text) / 2), y, color)
    }

    private fun font(): TextRenderer {
        return MinecraftClient.getInstance().textRenderer
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

            val diff = (otherRed - red).toDouble().pow(2.0) * 0.2126 + (otherGreen - green).toDouble().pow(2.0) * 0.7152 + (otherBlue - blue).toDouble().pow(2.0) * 0.0722

            if (bestFormatting == null || bestDiff > diff) {
                bestFormatting = formatting
                bestDiff = diff
            }
        }
        return bestFormatting!!
    }
}