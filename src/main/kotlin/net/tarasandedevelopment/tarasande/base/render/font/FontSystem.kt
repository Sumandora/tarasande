package net.tarasandedevelopment.tarasande.base.render.font

import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.base.render.blur.Blur
import net.tarasandedevelopment.tarasande.render.font.FontMinecraft
import java.awt.Color

class ManagerFont : Manager<Font>() {

    init {
        add(
            FontMinecraft()
        )
    }

    fun selected(): Font = list[TarasandeMain.get().clientValues.fontRenderer.let { it.settings.indexOf(it.selected[0]) }]
}

abstract class Font(val name: String) {

    abstract fun textOutline(matrices: MatrixStack?, text: String, x: Float, y: Float, color: Int = -1, outlineColor: Int = Color.black.rgb, scale: Float = 1F, centered: Boolean = false)
    abstract fun textShadow(matrices: MatrixStack?, text: String, x: Float, y: Float, color: Int = -1, scale: Float = 1F, offset: Float = 1.0F, centered: Boolean = false)
    abstract fun text(matrices: MatrixStack?, text: String, x: Float, y: Float, color: Int = -1, scale: Float = 1F, centered: Boolean = false)

    abstract fun getWidth(text: String): Int
    abstract fun trimToWidth(text: String, maxWidth: Int): String
    abstract fun fontHeight(): Int
}
