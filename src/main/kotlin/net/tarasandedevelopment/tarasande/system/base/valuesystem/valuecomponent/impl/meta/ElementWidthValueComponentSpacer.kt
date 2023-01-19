package net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.meta

import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.ValueSpacer
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import kotlin.math.floor

class ElementWidthValueComponentSpacer(value: Value) : ElementWidthValueComponent<ValueSpacer>(value) {

    private val lines = ArrayList<String>()

    override fun init() {
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        lines.clear()
        var str = value.name
        while (str.isNotEmpty()) {
            var trimmed = FontWrapper.trimToWidth(str, floor(width / value.scale).toInt())
            if (trimmed != str) {
                val orig = trimmed
                while (trimmed.isNotEmpty() && !trimmed.endsWith(" ")) {
                    trimmed = trimmed.substring(0, trimmed.length - 1)
                }
                if (trimmed.isEmpty())
                    trimmed = orig
            }
            lines.add(trimmed)
            str = str.substring(trimmed.length)
        }

        for ((index, line) in lines.withIndex()) {
            FontWrapper.textShadow(matrices,
                line,
                0.0F,
                (getHeight() / 2.0F + (index - (lines.size - 1) / 2.0) * (FontWrapper.fontHeight() * value.scale / 2.0F)).toFloat(),
                value.getColor(RenderUtil.isHovered(mouseX.toDouble(), mouseY.toDouble(), 0.0, 0.0, width, getHeight())).rgb,
                value.scale,
                0.5F
            )
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (RenderUtil.isHovered(mouseX, mouseY, 0.0, 0.0, width, getHeight())) {
            value.onClick(button)
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

    override fun getHeight() = FontWrapper.fontHeight().toDouble() * value.scale * (lines.size)
}