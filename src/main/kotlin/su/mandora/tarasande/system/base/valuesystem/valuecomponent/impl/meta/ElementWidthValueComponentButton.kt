package su.mandora.tarasande.system.base.valuesystem.valuecomponent.impl.meta

import net.minecraft.client.gui.DrawContext
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues
import su.mandora.tarasande.system.base.valuesystem.Value
import su.mandora.tarasande.system.base.valuesystem.impl.meta.ValueButton
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import su.mandora.tarasande.util.extension.minecraft.render.fill
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.util.render.font.FontWrapper
import java.awt.Color

open class ElementWidthValueComponentButton(value: Value) : ElementWidthValueComponent<ValueButton>(value) {

    override fun init() {
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val textWidth = FontWrapper.getWidth(value.name)

        context.fill(width - 4 - textWidth / 2, getHeight() / 2.0 - FontWrapper.fontHeight() / 2, width, getHeight() / 2.0 + FontWrapper.fontHeight() / 2, Int.MIN_VALUE)

        FontWrapper.textShadow(context,
            value.name,
            (width - 2 - textWidth / 2).toFloat(),
            (getHeight() / 2F - FontWrapper.fontHeight() * 0.25F).toFloat(),
            if (value.isEnabled())
                if (RenderUtil.isHovered(mouseX.toDouble(),
                        mouseY.toDouble(),
                        width - 4 - textWidth / 2,
                        getHeight() / 2.0 - FontWrapper.fontHeight() / 2,
                        width,
                        getHeight() / 2.0 + FontWrapper.fontHeight() / 2))
                    TarasandeValues.accentColor.getColor().rgb
                else
                    -1
            else
                Color.white.darker().darker().rgb,
            scale = 0.5F,
            offset = 0.5F)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val textWidth = FontWrapper.getWidth(value.name)

        if (value.isEnabled() && button == 0 && RenderUtil.isHovered(mouseX,
                mouseY,
                width - 4 - textWidth / 2,
                getHeight() / 2.0 - FontWrapper.fontHeight() / 2,
                width,
                getHeight() / 2.0 + FontWrapper.fontHeight() / 2)) {
            value.onClick()
            return true
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

    override fun getHeight() = FontWrapper.fontHeight() * 1.5
}