package net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl

import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.feature.clientvalue.ClientValues
import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import java.awt.Color
import kotlin.math.min

class ElementWidthValueComponentBoolean(value: Value) : ElementWidthValueComponent<ValueBoolean>(value) {

    private var toggleTime = 0L

    override fun init() {
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        FontWrapper.textShadow(matrices, value.name, 0.0F, (getHeight() * 0.5F - FontWrapper.fontHeight() * 0.5F * 0.5F).toFloat(), Color.white.let { if (value.isEnabled()) it else it.darker().darker() }.rgb, scale = 0.5F, offset = 0.5F)

        val expandedAnimation = min((System.currentTimeMillis() - toggleTime) / 100.0 /* length in ms */, 1.0)
        val fade = (if (value.value) expandedAnimation else 1.0 - expandedAnimation)

        var color = RenderUtil.colorInterpolate(Color.white, ClientValues.accentColor.getColor(), fade)
        var colorInverted = RenderUtil.colorInterpolate(ClientValues.accentColor.getColor(), Color.white, fade)

        if (!value.isEnabled()) {
            color = color.darker().darker()
            colorInverted = colorInverted.darker().darker()
        }

        RenderUtil.fill(matrices, width - 2 - 2 * fade, getHeight() / 2 - 2 * fade, width - 2 + 2 * fade, getHeight() / 2 + 2 * fade, color.rgb)
        RenderUtil.outlinedFill(matrices, width - 4, getHeight() / 2 - 2, width, getHeight() / 2 + 2, 2.0F, colorInverted.rgb)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0 && RenderUtil.isHovered(mouseX, mouseY, width - 4, getHeight() / 2 - 2, width, getHeight() / 2 + 2)) {
            value.value = !value.value
            toggleTime = System.currentTimeMillis()
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

    override fun getHeight() = FontWrapper.fontHeight().toDouble()
}