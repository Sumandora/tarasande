package su.mandora.tarasande.util.extension.minecraft.render

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import su.mandora.tarasande.util.render.RenderUtil

// Those are just the filled shapes, use RenderUtil for outlined ones.

fun DrawContext.fill(x1: Double, y1: Double, x2: Double, y2: Double, color: Int) {
    val colors = RenderUtil.colorToRGBA(color)
    val vertexConsumer = this.vertexConsumers.getBuffer(RenderLayer.getGui())
    val matrix4f = this.matrices.peek().positionMatrix
    vertexConsumer.vertex(matrix4f, x1.toFloat(), y1.toFloat(), 0F).color(colors[0], colors[1], colors[2], colors[3]).next()
    vertexConsumer.vertex(matrix4f, x1.toFloat(), y2.toFloat(), 0F).color(colors[0], colors[1], colors[2], colors[3]).next()
    vertexConsumer.vertex(matrix4f, x2.toFloat(), y2.toFloat(), 0F).color(colors[0], colors[1], colors[2], colors[3]).next()
    vertexConsumer.vertex(matrix4f, x2.toFloat(), y1.toFloat(), 0F).color(colors[0], colors[1], colors[2], colors[3]).next()
    this.completeDraw()
}

fun DrawContext.drawText(textRenderer: TextRenderer, text: String, x: Float, y: Float, color: Int, shadow: Boolean): Int {
    val i = textRenderer.draw(
        text,
        x,
        y,
        color,
        shadow,
        this.matrices.peek().positionMatrix,
        this.vertexConsumers,
        TextRenderer.TextLayerType.NORMAL,
        0,
        15728880,
        textRenderer.isRightToLeft
    )
    this.completeDraw()
    return i
}

fun DrawContext.drawText(textRenderer: TextRenderer, text: Text, x: Float, y: Float, color: Int, shadow: Boolean): Int {
    return drawText(textRenderer, text.asOrderedText(), x, y, color, shadow)
}

fun DrawContext.drawText(textRenderer: TextRenderer, text: OrderedText?, x: Float, y: Float, color: Int, shadow: Boolean): Int {
    val i = textRenderer.draw(
        text,
        x,
        y,
        color,
        shadow,
        this.matrices.peek().positionMatrix,
        this.vertexConsumers,
        TextRenderer.TextLayerType.NORMAL,
        0,
        15728880
    )
    this.completeDraw()
    return i
}

fun DrawContext.fillHorizontalGradient(startX: Double, startY: Double, endX: Double, endY: Double, colorStart: Int, colorEnd: Int) {
    val startColors = RenderUtil.colorToRGBA(colorStart)
    val endColors = RenderUtil.colorToRGBA(colorEnd)

    val vertexConsumer = this.vertexConsumers.getBuffer(RenderLayer.getGui())
    val matrix4f = this.matrices.peek().positionMatrix
    vertexConsumer.vertex(matrix4f, startX.toFloat(), startY.toFloat(), 0F).color(endColors[0], endColors[1], endColors[2], endColors[3]).next()
    vertexConsumer.vertex(matrix4f, startX.toFloat(), endY.toFloat(), 0F).color(endColors[0], endColors[1], endColors[2], endColors[3]).next()
    vertexConsumer.vertex(matrix4f, endX.toFloat(), endY.toFloat(), 0F).color(startColors[0], startColors[1], startColors[2], startColors[3]).next()
    vertexConsumer.vertex(matrix4f, endX.toFloat(), startY.toFloat(), 0F).color(startColors[0], startColors[1], startColors[2], startColors[3]).next()
    completeDraw()
}

fun DrawContext.fillVerticalGradient(startX: Double, startY: Double, endX: Double, endY: Double, colorStart: Int, colorEnd: Int) {
    val startColors = RenderUtil.colorToRGBA(colorStart)
    val endColors = RenderUtil.colorToRGBA(colorEnd)

    val vertexConsumer = this.vertexConsumers.getBuffer(RenderLayer.getGui())
    val matrix4f = this.matrices.peek().positionMatrix
    vertexConsumer.vertex(matrix4f, startX.toFloat(), startY.toFloat(), 0F).color(startColors[0], startColors[1], startColors[2], startColors[3]).next()
    vertexConsumer.vertex(matrix4f, startX.toFloat(), endY.toFloat(), 0F).color(endColors[0], endColors[1], endColors[2], endColors[3]).next()
    vertexConsumer.vertex(matrix4f, endX.toFloat(), endY.toFloat(), 0F).color(endColors[0], endColors[1], endColors[2], endColors[3]).next()
    vertexConsumer.vertex(matrix4f, endX.toFloat(), startY.toFloat(), 0F).color(startColors[0], startColors[1], startColors[2], startColors[3]).next()
    completeDraw()
}

fun DrawContext.completeDraw() {
    // Should the method be removed, put its replacement here
    @Suppress("DEPRECATION")
    tryDraw()
}