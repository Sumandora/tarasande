package su.mandora.tarasande.util.render.font

import net.minecraft.client.gui.DrawContext
import su.mandora.tarasande.mc
import su.mandora.tarasande.util.extension.minecraft.render.drawText
import su.mandora.tarasande.util.string.StringUtil
import java.awt.Color

object FontWrapper {

    private val mcInternal = mc.textRenderer

    fun textShadow(context: DrawContext, text: String, x: Float, y: Float, color: Int = -1, scale: Float = 1F, offset: Float = 1F, centered: Boolean = false) {
        text(context, StringUtil.stripColors(text), x + offset, y + offset, Color(color, true).darker().darker().darker().darker().rgb, scale, centered)
        text(context, text, x, y, color, scale, centered)
    }

    fun text(context: DrawContext, text: String, x: Float, y: Float, color: Int = -1, scale: Float = 1F, centered: Boolean = false) {
        context.matrices.push()

        if (scale != 1F) {
            context.matrices.translate(x.toDouble(), y.toDouble(), 0.0)
            context.matrices.scale(scale, scale, 1F)
            context.matrices.translate(-(x.toDouble()), -y.toDouble(), 0.0)
        }

        context.drawText(mcInternal, text, (if (centered) x - (getWidth(text) * 0.5F) else x), y, color, false)

        context.matrices.pop()
    }

    fun getWidth(text: String) = mcInternal.getWidth(text)
    fun trimToWidth(text: String, maxWidth: Int): String = mcInternal.trimToWidth(text, maxWidth)
    fun fontHeight() = mcInternal.fontHeight
}
