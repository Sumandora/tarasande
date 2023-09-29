package su.mandora.tarasande.system.base.valuesystem.valuecomponent.impl

import net.minecraft.client.gui.DrawContext
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues
import su.mandora.tarasande.system.base.valuesystem.Value
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.util.render.font.FontWrapper
import java.awt.Color

class ElementWidthValueComponentMode(value: Value) : ElementWidthValueComponent<ValueMode>(value) {

    override fun init() {
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        FontWrapper.textShadow(context, value.name, 0F, (getHeight() * 0.5F - FontWrapper.fontHeight() * 0.5F * 0.5F).toFloat(), Color.white.let { if (value.isEnabled()) it else it.darker().darker() }.rgb, scale = 0.5F, offset = 0.5F)

        for ((index, setting) in value.values.withIndex()) {
            var color = if (value.isSelected(setting)) TarasandeValues.accentColor.getColor() else Color.white
            if (!value.isEnabled()) color = color.darker().darker()
            FontWrapper.textShadow(context, setting, (width - FontWrapper.getWidth(setting) / 2F).toFloat(), (getHeight() / 2F + (index - (value.values.size - 1) / 2.0 + 0.5) * (FontWrapper.fontHeight() / 2F) - FontWrapper.fontHeight() / 2F).toFloat(), color.rgb, scale = 0.5F, offset = 0.5F)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0 || !value.isEnabled()) return false
        for ((index, setting) in value.values.withIndex()) {
            val x = width - FontWrapper.getWidth(setting) / 2F
            val y = getHeight() / 2F + (index - (this.value.values.size - 1) / 2.0 + 0.5) * (FontWrapper.fontHeight() / 2F) - FontWrapper.fontHeight() / 2F
            if (RenderUtil.isHovered(mouseX, mouseY, x, y, width, y + FontWrapper.fontHeight() / 2F)) {
                value.select(index)
                return true
            }
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double) = false

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int) = false

    override fun charTyped(chr: Char, modifiers: Int) {
    }

    override fun tick() {
    }

    override fun onClose() {
    }

    override fun getHeight() = (value.values.size + 1) * FontWrapper.fontHeight().toDouble() / 2
}