package su.mandora.tarasande.system.base.valuesystem.valuecomponent.impl

import net.minecraft.client.gui.DrawContext
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues
import su.mandora.tarasande.system.base.valuesystem.Value
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import su.mandora.tarasande.util.extension.minecraft.render.fill
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.util.render.font.FontWrapper
import java.awt.Color
import kotlin.math.min

class ElementWidthValueComponentBoolean(value: Value) : ElementWidthValueComponent<ValueBoolean>(value) {

    private var toggleTime = 0L

    override fun init() {
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        FontWrapper.textShadow(context, value.name, 0F, (getHeight() * 0.5F - FontWrapper.fontHeight() * 0.5F * 0.5F).toFloat(), Color.white.let { if (value.isEnabled()) it else it.darker().darker() }.rgb, scale = 0.5F, offset = 0.5F)

        val expandedAnimation = min((System.currentTimeMillis() - toggleTime) / 100.0 /* length in ms */, 1.0)
        val fade = (if (value.value) expandedAnimation else 1.0 - expandedAnimation)

        var color = RenderUtil.colorInterpolate(Color.white, TarasandeValues.accentColor.getColor(), fade)
        var colorInverted = RenderUtil.colorInterpolate(TarasandeValues.accentColor.getColor(), Color.white, fade)

        if (!value.isEnabled()) {
            color = color.darker().darker()
            colorInverted = colorInverted.darker().darker()
        }

        context.fill(width - 2 - 2 * fade, getHeight() / 2 - 2 * fade, width - 2 + 2 * fade, getHeight() / 2 + 2 * fade, color.rgb)
        RenderUtil.outlinedFill(context.matrices, width - 4, getHeight() / 2 - 2, width, getHeight() / 2 + 2, 2F, colorInverted.rgb)
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

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double) = false

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int) = false

    override fun charTyped(chr: Char, modifiers: Int) {
    }

    override fun tick() {
    }

    override fun onClose() {
    }

    override fun getHeight() = FontWrapper.fontHeight().toDouble()
}