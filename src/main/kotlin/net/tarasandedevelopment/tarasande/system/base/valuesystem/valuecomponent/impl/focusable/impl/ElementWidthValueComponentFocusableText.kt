package net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.focusable.impl

import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.feature.tarasandevalue.TarasandeValues
import net.tarasandedevelopment.tarasande.injection.accessor.ITextFieldWidget
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueText
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.focusable.ElementWidthValueComponentFocusable
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import net.tarasandedevelopment.tarasande.util.screen.widget.TextFieldWidgetPlaceholder
import org.lwjgl.glfw.GLFW
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

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        textFieldWidget.width = (width * (1.0 / scale)).toInt()
        if (textFieldWidget.isFocused && value.isEnabled())
            textFieldAccessor.tarasande_setColor(TarasandeValues.accentColor.getColor())
        else
            textFieldWidget.setCursorToEnd()

        if (!value.isEnabled()) {
            textFieldAccessor.tarasande_setColor(Color.white.darker().darker())
            textFieldWidget.setTextFieldFocused(false)
        }

        matrices.push()
        if (centered)
        // scary multiplication
            matrices.translate(0.0, getHeight() * scale * scale * 0.5, 0.0)
        matrices.scale(scale, scale, 1.0F)
        textFieldWidget.render(matrices, mouseX, mouseY, delta)
        matrices.pop()
        textFieldAccessor.tarasande_setColor(null)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0) return false
        if (value.isEnabled() && RenderUtil.isHovered(mouseX, mouseY, 0.0, 0.0, width, getHeight())) { // hacky fix for size hacks
            textFieldWidget.setTextFieldFocused(true)
        } else {
            textFieldWidget.setTextFieldFocused(false)
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double) = false

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return if (textFieldWidget.isFocused && (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
            textFieldWidget.setTextFieldFocused(false)
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
        textFieldWidget.setTextFieldFocused(false)
    }

    override fun isFocused() = textFieldWidget.isFocused

    override fun getHeight() = FontWrapper.fontHeight().toDouble() * (scale + 0.5)
}