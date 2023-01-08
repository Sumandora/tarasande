package net.tarasandedevelopment.tarasande.util.render.font

import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.tarasandedevelopment.tarasande.util.extension.mc
import java.awt.Color

object FontWrapper {

    private val mcInternal = mc.textRenderer

    fun textOutline(matrices: MatrixStack, text: String, x: Float, y: Float, color: Int = -1, outlineColor: Int = Color.black.rgb, scale: Float = 1F, centered: Boolean = false) {
        val immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().buffer)

        matrices.push()

        if (scale != 1.0F) {
            matrices.translate(x.toDouble(), y.toDouble(), 0.0)
            matrices.scale(scale, scale, 1F)
            matrices.translate(-(x.toDouble()), -y.toDouble(), 0.0)
        }

        mcInternal.drawWithOutline(Text.of(text).asOrderedText(), (if (centered) x - getWidth(text) * 0.5F else x), y, color, outlineColor, matrices.peek()?.positionMatrix, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE)
        matrices.pop()

        immediate.draw()
    }

    fun textShadow(matrices: MatrixStack, text: String, x: Float, y: Float, color: Int = -1, scale: Float = 1F, offset: Float = 1.0F, centered: Boolean = false) {
        text(matrices, Formatting.strip(text)!!, x + offset, y + offset, Color(color, true).darker().darker().darker().darker().rgb, scale, centered)
        text(matrices, text, x, y, color, scale, centered)
    }

    fun text(matrices: MatrixStack, text: String, x: Float, y: Float, color: Int = -1, scale: Float = 1F, centered: Boolean = false) {
        matrices.push()

        if (scale != 1.0F) {
            matrices.translate(x.toDouble(), y.toDouble(), 0.0)
            matrices.scale(scale, scale, 1F)
            matrices.translate(-(x.toDouble()), -y.toDouble(), 0.0)
        }

        mcInternal.draw(matrices, text, (if (centered) x - (getWidth(text) * 0.5F) else x), y, color)

        matrices.pop()
    }

    fun getWidth(text: String) = mcInternal.getWidth(text)
    fun trimToWidth(text: String, maxWidth: Int): String = mcInternal.trimToWidth(text, maxWidth)
    fun fontHeight() = mcInternal.fontHeight
}
