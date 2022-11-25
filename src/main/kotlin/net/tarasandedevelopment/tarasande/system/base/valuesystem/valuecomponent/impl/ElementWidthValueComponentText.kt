package net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.injection.accessor.ITextFieldWidget
import net.tarasandedevelopment.tarasande.screen.widget.textfield.TextFieldWidgetPlaceholder
import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueText
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import org.lwjgl.glfw.GLFW
import java.awt.Color

class ElementWidthValueComponentText(value: Value) : ElementWidthValueComponent(value) {

    var scale = 0.5F
    var centered = false

    constructor(value: Value, scale: Float, centered: Boolean = true) : this(value) {
        this.scale = scale
        this.centered = centered
    }

    //TODO
    val textFieldWidget = TextFieldWidgetPlaceholder(MinecraftClient.getInstance().textRenderer, 1, 1, 1, getHeight().toInt() - 1, Text.of((value as ValueText).name))

    init {
        textFieldWidget.setMaxLength(Int.MAX_VALUE)
        textFieldWidget.setDrawsBackground(false)
    }

    override fun init() {
        val valueText = value as ValueText
        textFieldWidget.text = valueText.value
        textFieldWidget.setChangedListener {
            valueText.value = it
            valueText.onChange()
        }
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        textFieldWidget.width = (width * (1.0 / scale)).toInt()
        if (textFieldWidget.isFocused && value.isEnabled())
            (textFieldWidget as ITextFieldWidget).tarasande_setColor(TarasandeMain.clientValues().accentColor.getColor())
        else
            textFieldWidget.setCursorToEnd()

        if (!value.isEnabled()) {
            (textFieldWidget as ITextFieldWidget).tarasande_setColor(Color.white.darker().darker())
            textFieldWidget.setTextFieldFocused(false)
        }

        matrices?.push()
        if (centered)
        // scary multiplication
            matrices?.translate(0.0, getHeight() * scale * scale * 0.5, 0.0)
        matrices?.scale(scale, scale, 1.0F)
        textFieldWidget.render(matrices, mouseX, mouseY, delta)
        matrices?.pop()
        (textFieldWidget as ITextFieldWidget).tarasande_setColor(null)
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

    fun isFocused() = textFieldWidget.isFocused

    fun setFocused(focused: Boolean) = textFieldWidget.setTextFieldFocused(focused)

    override fun getHeight() = FontWrapper.fontHeight().toDouble() * (scale + 0.5)
}