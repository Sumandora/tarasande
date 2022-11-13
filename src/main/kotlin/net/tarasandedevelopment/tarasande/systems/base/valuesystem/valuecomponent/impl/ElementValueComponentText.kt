package net.tarasandedevelopment.tarasande.systems.base.valuesystem.valuecomponent.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.mixin.accessor.ITextFieldWidget
import net.tarasandedevelopment.tarasande.screen.widget.textfield.TextFieldWidgetPlaceholder
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueText
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.valuecomponent.ElementValueComponent
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import org.lwjgl.glfw.GLFW
import java.awt.Color

class ElementValueComponentText(value: Value) : ElementValueComponent(value) {
    //TODO
    val textFieldWidget = TextFieldWidgetPlaceholder(MinecraftClient.getInstance().textRenderer, 1, 1, 1, (getHeight() * 2).toInt() - 1, Text.of((value as ValueText).name))

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
        textFieldWidget.width = (width * 2).toInt()
        matrices?.push()
        matrices?.scale(0.5F, 0.5F, 1.0F)

        if (textFieldWidget.isFocused)
            (textFieldWidget as ITextFieldWidget).tarasande_setColor(TarasandeMain.clientValues().accentColor.getColor())
        else
            textFieldWidget.setCursorToEnd()

        if (!value.isEnabled())
            (textFieldWidget as ITextFieldWidget).tarasande_setColor(Color.white.darker().darker())

        textFieldWidget.render(matrices, mouseX, mouseY, delta)
        (textFieldWidget as ITextFieldWidget).tarasande_setColor(null)
        matrices?.pop()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0) return false
        if (value.isEnabled() && RenderUtil.isHovered(mouseX, mouseY, 1.0, 1.0, width, getHeight())) { // hacky fix for size hacks
            textFieldWidget.mouseClicked(width * 2, getHeight() / 2.0F, button)
        } else {
            textFieldWidget.mouseClicked(-1.0, -1.0, button)
            textFieldWidget.setCursorToEnd()
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double) = false

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (textFieldWidget.isFocused && (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
            textFieldWidget.setTextFieldFocused(false)
            textFieldWidget.setCursorToEnd()
            return true
        } else
            textFieldWidget.keyPressed(keyCode, scanCode, modifiers)
        return false
    }

    override fun charTyped(chr: Char, modifiers: Int) {
        textFieldWidget.charTyped(chr, modifiers)
    }

    override fun tick() {
    }

    override fun onClose() {
        textFieldWidget.setTextFieldFocused(false)
        textFieldWidget.setCursorToEnd()
    }

    fun isFocused() = textFieldWidget.isFocused

    fun setFocused(focused: Boolean) = textFieldWidget.setTextFieldFocused(focused)

    override fun getHeight() = FontWrapper.fontHeight().toDouble()
}