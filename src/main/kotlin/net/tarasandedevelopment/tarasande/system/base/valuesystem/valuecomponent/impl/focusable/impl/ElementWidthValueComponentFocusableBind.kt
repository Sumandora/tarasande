package net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.focusable.impl

import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBind
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.focusable.ElementWidthValueComponentFocusable
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import org.lwjgl.glfw.GLFW
import java.awt.Color

class ElementWidthValueComponentFocusableBind(value: Value) : ElementWidthValueComponentFocusable(value) {

    private var waitsForInput = false

    override fun init() {
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        val valueBind = value as ValueBind

        val white = Color.white.let { if (valueBind.isEnabled()) it else it.darker().darker() }

        FontWrapper.textShadow(matrices, value.name, 0.0F, (getHeight() * 0.5F - FontWrapper.fontHeight() * 0.5F * 0.5F).toFloat(), white.rgb, scale = 0.5F, offset = 0.5F)

        var name = RenderUtil.getBindName(valueBind.type, valueBind.button)
        if (waitsForInput) {
            name = "_"
        }
        val textWidth = FontWrapper.getWidth(name)

        RenderUtil.fill(matrices, width - textWidth / 2, getHeight() * 0.25, width, getHeight() * 0.75, Int.MIN_VALUE)
        FontWrapper.textShadow(matrices, name, (width - textWidth / 2).toFloat(), (getHeight() * 0.25F).toFloat(), white.rgb, scale = 0.5F, offset = 0.5F)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val valueBind = value as ValueBind

        var name = RenderUtil.getBindName(valueBind.type, valueBind.button)
        if (waitsForInput) {
            name = "_"
        }
        val textWidth = FontWrapper.getWidth(name)

        if (button == 0 && RenderUtil.isHovered(mouseX, mouseY, width - textWidth / 2, getHeight() * 0.25, width, getHeight() * 0.75)) {
            waitsForInput = !waitsForInput
            return true
        } else {
            if (waitsForInput && valueBind.mouse) {
                if (valueBind.filter(ValueBind.Type.MOUSE, button)) {
                    val oldType = valueBind.type
                    val oldButton = valueBind.button
                    valueBind.type = ValueBind.Type.MOUSE
                    valueBind.button = button
                }
                waitsForInput = false
                return true
            }
            waitsForInput = false
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double) = false

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (waitsForInput) {
            val valueBind = value as ValueBind
            val key = if (keyCode == GLFW.GLFW_KEY_ESCAPE) GLFW.GLFW_KEY_UNKNOWN else keyCode
            if (valueBind.filter(ValueBind.Type.KEY, key)) {
                val oldType = valueBind.type
                val oldButton = valueBind.button
                valueBind.type = ValueBind.Type.KEY
                valueBind.button = key
            }
            waitsForInput = false
            return true
        }
        return false
    }

    override fun charTyped(chr: Char, modifiers: Int) {
    }

    override fun tick() {
    }

    override fun onClose() {
        waitsForInput = false
    }

    override fun getHeight() = FontWrapper.fontHeight().toDouble()

    override fun isFocused() = waitsForInput
}