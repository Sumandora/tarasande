package net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl

import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.feature.clientvalue.ClientValues
import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import java.awt.Color

class ElementWidthValueComponentMode(value: Value) : ElementWidthValueComponent(value) {

    override fun init() {
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        val valueMode = value as ValueMode

        FontWrapper.textShadow(matrices, value.name, 0.0F, (getHeight() * 0.5F - FontWrapper.fontHeight() * 0.5F * 0.5F).toFloat(), Color.white.let { if (value.isEnabled()) it else it.darker().darker() }.rgb, scale = 0.5F, offset = 0.5F)

        for ((index, setting) in valueMode.values.withIndex()) {
            var color = if (valueMode.selected.contains(setting)) ClientValues.accentColor.getColor() else Color.white
            if (!valueMode.isEnabled()) color = color.darker().darker()
            FontWrapper.textShadow(matrices, setting, (width - FontWrapper.getWidth(setting) / 2f).toFloat(), (getHeight() / 2.0F + (index - (valueMode.values.size - 1) / 2.0 + 0.5) * (FontWrapper.fontHeight() / 2.0F) - FontWrapper.fontHeight() / 2.0F).toFloat(), color.rgb, scale = 0.5F, offset = 0.5F)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0 || !value.isEnabled()) return false
        val valueMode = value as ValueMode
        for ((index, setting) in valueMode.values.withIndex()) {
            val x = width - FontWrapper.getWidth(setting) / 2.0F
            val y = getHeight() / 2.0F + (index - (valueMode.values.size - 1) / 2.0 + 0.5) * (FontWrapper.fontHeight() / 2.0F) - FontWrapper.fontHeight() / 2.0F
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

    override fun getHeight() = ((value as ValueMode).values.size + 1) * FontWrapper.fontHeight().toDouble() / 2
}