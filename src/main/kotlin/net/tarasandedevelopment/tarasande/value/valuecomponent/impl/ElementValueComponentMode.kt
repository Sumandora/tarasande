package net.tarasandedevelopment.tarasande.value.impl.Valuecomponent.impl

import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import net.tarasandedevelopment.tarasande.value.Value
import net.tarasandedevelopment.tarasande.value.impl.ValueMode
import net.tarasandedevelopment.tarasande.value.impl.Valuecomponent.ElementValueComponent
import java.awt.Color

class ElementValueComponentMode(value: Value) : ElementValueComponent(value) {

    override fun init() {
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val valueMode = value as ValueMode

        FontWrapper.textShadow(matrices, value.name, 0.0F, (getHeight() / 2.0F - FontWrapper.fontHeight() / 2.0F).toFloat(), Color.white.let { if (value.isEnabled()) it else it.darker().darker() }.rgb, scale = 0.5F, offset = 0.5F)

        for ((index, setting) in valueMode.settings.withIndex()) {
            var color = if (valueMode.selected.contains(setting)) TarasandeMain.get().clientValues.accentColor.getColor() else Color.white
            if (!valueMode.isEnabled()) color = color.darker().darker()
            FontWrapper.textShadow(matrices, setting, (width - FontWrapper.getWidth(setting) / 2f).toFloat(), (getHeight() / 2.0F + (index - (valueMode.settings.size - 1) / 2.0 + 0.5) * (FontWrapper.fontHeight() / 2.0f) - FontWrapper.fontHeight() / 2.0F).toFloat(), color.rgb, scale = 0.5F, offset = 0.5F)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0) return false
        val valueMode = value as ValueMode
        for ((index, setting) in valueMode.settings.withIndex()) {
            val x = width - FontWrapper.getWidth(setting) / 2.0f
            val y = getHeight() / 2.0F + (index - (valueMode.settings.size - 1) / 2.0 + 0.5) * (FontWrapper.fontHeight() / 2.0f) - FontWrapper.fontHeight() / 2.0F
            if (RenderUtil.isHovered(mouseX, mouseY, x, y, width, y + FontWrapper.fontHeight() / 2.0F)) {
                valueMode.select(index)
                valueMode.onChange()
                return true
            }
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double) = false

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int) = false

    override fun charTyped(chr: Char, modifiers: Int) {
    }

    override fun tick() {
    }

    override fun onClose() {
    }

    override fun getHeight() = ((value as ValueMode).settings.size + 1) * FontWrapper.fontHeight().toDouble() / 2
}