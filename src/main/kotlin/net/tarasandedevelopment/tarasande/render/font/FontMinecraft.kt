package net.tarasandedevelopment.tarasande.render.font

import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.tarasandedevelopment.tarasande.base.render.font.Font
import java.awt.Color

class FontMinecraft : Font("Minecraft") {

    private val mcInternal = MinecraftClient.getInstance().textRenderer

    override fun textOutline(matrices: MatrixStack?, text: String, x: Float, y: Float, color: Int, outlineColor: Int, scale: Float, centered: Boolean) {
        val immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().buffer)

        matrices?.push()

        if (scale != 1.0F) {
            matrices?.translate(x.toDouble(), y.toDouble(), 0.0)
            matrices?.scale(scale, scale, 1F)
            matrices?.translate(-(x.toDouble()), -y.toDouble(), 0.0)
        }

        mcInternal.drawWithOutline(Text.of(text).asOrderedText(), (if (centered) x - getWidth(text) * 0.5f else x), y, color, outlineColor, matrices?.peek()?.positionMatrix, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE)
        matrices?.pop()
        
        immediate.draw()
    }

    override fun textShadow(matrices: MatrixStack?, text: String, x: Float, y: Float, color: Int, scale: Float, offset: Float, centered: Boolean) {
        text(matrices, Formatting.strip(text)!!, x + offset, y + offset, Color(color, true).darker().darker().darker().darker().rgb, scale, centered)
        text(matrices, text, x, y, color, scale, centered)
    }

    override fun text(matrices: MatrixStack?, text: String, x: Float, y: Float, color: Int, scale: Float, centered: Boolean) {
        matrices?.push()

        if (scale != 1.0F) {
            matrices?.translate(x.toDouble(), y.toDouble(), 0.0)
            matrices?.scale(scale, scale, 1F)
            matrices?.translate(-(x.toDouble()), -y.toDouble(), 0.0)
        }

        mcInternal.draw(matrices, text, (if (centered) x - (getWidth(text) * 0.5f) else x), y, color)

        matrices?.pop()
    }

    override fun getWidth(text: String) = mcInternal.getWidth(text)
    override fun trimToWidth(text: String, maxWidth: Int): String = mcInternal.trimToWidth(text, maxWidth)
    override fun fontHeight() = mcInternal.fontHeight
}
