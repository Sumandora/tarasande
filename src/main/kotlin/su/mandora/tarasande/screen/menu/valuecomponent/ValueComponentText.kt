package su.mandora.tarasande.screen.menu.valuecomponent

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.screen.menu.valuecomponent.ValueComponent
import su.mandora.tarasande.base.value.Value
import su.mandora.tarasande.mixin.accessor.ITextFieldWidget
import su.mandora.tarasande.screen.accountmanager.elements.TextFieldWidgetPlaceholder
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.value.ValueText
import java.awt.Color

class ValueComponentText(value: Value) : ValueComponent(value) {

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
            (textFieldWidget as ITextFieldWidget).tarasande_setColor(TarasandeMain.get().clientValues.accentColor.getColor())
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
        if (RenderUtil.isHovered(mouseX, mouseY, 1.0, 1.0, width, getHeight())) { // hacky fix for size hacks
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

    override fun getHeight() = MinecraftClient.getInstance().textRenderer.fontHeight.toDouble()
}