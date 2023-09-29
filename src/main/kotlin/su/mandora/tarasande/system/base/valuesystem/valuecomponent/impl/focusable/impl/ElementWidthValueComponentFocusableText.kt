package su.mandora.tarasande.system.base.valuesystem.valuecomponent.impl.focusable.impl

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues
import su.mandora.tarasande.injection.accessor.ITextFieldWidget
import su.mandora.tarasande.injection.accessor.ITextRenderer
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.Value
import su.mandora.tarasande.system.base.valuesystem.impl.ValueText
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.impl.focusable.ElementWidthValueComponentFocusable
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.util.render.font.FontWrapper
import su.mandora.tarasande.util.screen.widget.TextFieldWidgetPlaceholder
import java.awt.Color

class ElementWidthValueComponentFocusableText(value: Value) : ElementWidthValueComponentFocusable<ValueText>(value) {

    var scale = 0.5F
    var centered = false

    constructor(value: Value, scale: Float, centered: Boolean = true) : this(value) {
        this.scale = scale
        this.centered = centered
    }

    //TODO
    val textFieldWidget = TextFieldWidgetPlaceholder(mc.textRenderer, 1, 1, 1, getHeight().toInt() - 1, Text.of((value as ValueText).name))
    private val textFieldAccessor = textFieldWidget as ITextFieldWidget

    init {
        textFieldAccessor.tarasande_disableSelectionHighlight()
        textFieldWidget.setMaxLength(Int.MAX_VALUE)
        textFieldWidget.setDrawsBackground(false)
    }

    override fun init() {
        textFieldWidget.text = value.value
        textFieldWidget.setChangedListener {
            value.value = it
        }
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        textFieldWidget.width = (width * (1.0 / scale)).toInt()
        if (textFieldWidget.isFocused && value.isEnabled())
            textFieldAccessor.tarasande_setColor(TarasandeValues.accentColor.getColor())
        else
            textFieldWidget.setCursorToEnd(false)

        if (!value.isEnabled()) {
            textFieldAccessor.tarasande_setColor(Color.white.darker().darker())
            textFieldWidget.isFocused = false
        }

        context.matrices.push()
        if (centered)
        // scary multiplication
            context.matrices.translate(0.0, getHeight() * scale * scale * 0.5, 0.0)
        context.matrices.scale(scale, scale, 1F)
        val prev = (mc.textRenderer as ITextRenderer).tarasande_isDisableForwardShift()
        (mc.textRenderer as ITextRenderer).tarasande_setDisableForwardShift(true)
        textFieldWidget.render(context, mouseX, mouseY, delta)
        (mc.textRenderer as ITextRenderer).tarasande_setDisableForwardShift(prev)
        context.matrices.pop()
        textFieldAccessor.tarasande_setColor(null)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0) return false
        // hacky fix for size hacks
        textFieldWidget.isFocused = value.isEnabled() && RenderUtil.isHovered(mouseX, mouseY, 0.0, 0.0, width, getHeight())
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double) = false

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return if (textFieldWidget.isFocused && (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
            textFieldWidget.isFocused = false
            true
        } else
            textFieldWidget.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun charTyped(chr: Char, modifiers: Int) {
        textFieldWidget.charTyped(chr, modifiers)
    }

    override fun tick() {
    }

    override fun onClose() {
        textFieldWidget.isFocused = false
    }

    override fun isFocused() = textFieldWidget.isFocused

    override fun getHeight() = FontWrapper.fontHeight().toDouble() * (scale + 0.5)
}