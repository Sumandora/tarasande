package net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.meta

import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.ValueButton
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import java.awt.Color

class ElementWidthValueComponentButton(value: Value) : ElementWidthValueComponent(value) {

    override fun init() {
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val valueButton = value as ValueButton

        val textWidth = FontWrapper.getWidth(valueButton.name)

        RenderUtil.fill(matrices, width - 4 - textWidth / 2, getHeight() / 2.0 - FontWrapper.fontHeight() / 2, width, getHeight() / 2.0 + FontWrapper.fontHeight() / 2, Int.MIN_VALUE)

        FontWrapper.textShadow(matrices,
            value.name,
            (width - 2 - textWidth / 2).toFloat(),
            (getHeight() / 2.0F - FontWrapper.fontHeight() * 0.25F).toFloat(),
            if (valueButton.isEnabled())
                if (RenderUtil.isHovered(mouseX.toDouble(),
                        mouseY.toDouble(),
                        width - 4 - textWidth / 2,
                        getHeight() / 2.0 - FontWrapper.fontHeight() / 2,
                        width,
                        getHeight() / 2.0 + FontWrapper.fontHeight() / 2))
                    TarasandeMain.clientValues().accentColor.getColor().rgb
                else
                    -1
            else
                Color.white.darker().darker().rgb,
            scale = 0.5F,
            offset = 0.5F)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val valueButton = value as ValueButton

        val textWidth = FontWrapper.getWidth(valueButton.name)

        if (valueButton.isEnabled() && button == 0 && RenderUtil.isHovered(mouseX,
                mouseY,
                width - 4 - textWidth / 2,
                getHeight() / 2.0 - FontWrapper.fontHeight() / 2,
                width,
                getHeight() / 2.0 + FontWrapper.fontHeight() / 2)) {
            valueButton.onChange()
            return true
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

    override fun getHeight() = FontWrapper.fontHeight() * 1.5
}