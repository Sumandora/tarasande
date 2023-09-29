package su.mandora.tarasande.system.base.valuesystem.valuecomponent.impl.focusable.impl

import net.minecraft.client.gui.DrawContext
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.system.base.valuesystem.Value
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBind
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.impl.focusable.ElementWidthValueComponentFocusable
import su.mandora.tarasande.util.extension.minecraft.render.fill
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.util.render.font.FontWrapper
import java.awt.Color

class ElementWidthValueComponentFocusableBind(value: Value) : ElementWidthValueComponentFocusable<ValueBind>(value) {

    private var waitsForInput = false

    override fun init() {
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val white = Color.white.let { if (value.isEnabled()) it else it.darker().darker() }

        FontWrapper.textShadow(context, value.name, 0F, (getHeight() * 0.5F - FontWrapper.fontHeight() * 0.5F * 0.5F).toFloat(), white.rgb, scale = 0.5F, offset = 0.5F)

        var name = RenderUtil.getBindName(value.type, value.button)
        if (waitsForInput) {
            name = "_"
        }
        val textWidth = FontWrapper.getWidth(name)

        context.fill(width - textWidth / 2, getHeight() * 0.25, width, getHeight() * 0.75, Int.MIN_VALUE)
        FontWrapper.textShadow(context, name, (width - textWidth / 2).toFloat(), (getHeight() * 0.25F).toFloat(), white.rgb, scale = 0.5F, offset = 0.5F)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        var name = RenderUtil.getBindName(value.type, value.button)
        if (waitsForInput) {
            name = "_"
        }
        val textWidth = FontWrapper.getWidth(name)

        if (button == 0 && RenderUtil.isHovered(mouseX, mouseY, width - textWidth / 2, getHeight() * 0.25, width, getHeight() * 0.75)) {
            waitsForInput = !waitsForInput
            return true
        } else {
            if (waitsForInput && value.mouse) {
                if (value.filter(ValueBind.Type.MOUSE, button)) {
                    value.type = ValueBind.Type.MOUSE
                    value.button = button
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

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double) = false

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (waitsForInput) {
            val key = if (keyCode == GLFW.GLFW_KEY_ESCAPE) GLFW.GLFW_KEY_UNKNOWN else keyCode
            if (value.filter(ValueBind.Type.KEY, key)) {
                value.type = ValueBind.Type.KEY
                value.button = key
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