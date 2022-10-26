package net.tarasandedevelopment.tarasande.screen.cheatmenu.valuecomponent

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.valuecomponent.ElementValueComponent
import net.tarasandedevelopment.tarasande.base.value.Value
import net.tarasandedevelopment.tarasande.mixin.accessor.ITextFieldWidget
import net.tarasandedevelopment.tarasande.screen.widget.textfields.TextFieldWidgetPlaceholder
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.value.ValueTextList
import org.lwjgl.glfw.GLFW
import java.awt.Color

class ElementValueComponentTextList(value: Value) : ElementValueComponent(value) {
    private val textFieldWidget = TextFieldWidgetPlaceholder(MinecraftClient.getInstance().textRenderer, 0, 0, 40 * 2, MinecraftClient.getInstance().textRenderer.fontHeight * 2 - 1, Text.of("Input text"))

    init {
        textFieldWidget.setMaxLength(Int.MAX_VALUE)
        textFieldWidget.setDrawsBackground(false)
    }

    override fun init() {
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val valueTextList = value as ValueTextList

        val white = Color.white.let { if (valueTextList.isEnabled()) it else it.darker().darker() }

        matrices?.push()
        matrices?.translate(0.0, getHeight() / 2.0, 0.0)
        matrices?.scale(0.5F, 0.5F, 1.0F)
        matrices?.translate(0.0, -getHeight() / 2.0, 0.0)
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, value.name, 0.0F, (getHeight() / 2.0F - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F).toFloat(), white.rgb)
        matrices?.pop()

        for ((index, key) in valueTextList.value.withIndex()) {
            matrices?.push()
            matrices?.scale(0.5F, 0.5F, 1.0F)
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices,
                key,
                (width.toFloat() - MinecraftClient.getInstance().textRenderer.getWidth(key) / 2.0F) * 2.0F,
                MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * (index + 0.5F) * 2.0F,
                if (valueTextList.isEnabled())
                    if (RenderUtil.isHovered(
                            mouseX.toDouble(),
                            mouseY.toDouble(),
                            (width.toFloat() - MinecraftClient.getInstance().textRenderer.getWidth(key) / 2.0F).toDouble(),
                            (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * (index + 0.5F)).toDouble(),
                            width,
                            (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * ((index + 1) + 0.5F)).toDouble()))
                        TarasandeMain.get().clientValues.accentColor.getColor().rgb
                    else
                        -1
                else
                    Color.white.darker().darker().rgb)
            matrices?.pop()
        }
        RenderUtil.fill(matrices, width.toFloat() - 25.0, (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * (valueTextList.value.size + 0.5F)).toDouble() + 1.0, width, (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * (valueTextList.value.size + 0.5F)).toDouble() + 1.5, white.rgb)

        matrices?.push()
        matrices?.translate(width - 40, MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * (valueTextList.value.size + 0.5F) + 2.0, 0.0)
        matrices?.scale(0.5F, 0.5F, 1.0F)
        if (textFieldWidget.isFocused) (textFieldWidget as ITextFieldWidget).tarasande_setColor(TarasandeMain.get().clientValues.accentColor.getColor())
        if (!value.isEnabled()) (textFieldWidget as ITextFieldWidget).tarasande_setColor(Color.white.darker().darker())
        textFieldWidget.render(matrices, mouseX, mouseY, delta)
        (textFieldWidget as ITextFieldWidget).tarasande_setColor(null)
        matrices?.pop()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0) return false
        val valueTextList = value as ValueTextList

        if (RenderUtil.isHovered(mouseX, mouseY, width - 40, MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * (valueTextList.value.size + 0.25) + 2.0, width, (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * valueTextList.value.size + MinecraftClient.getInstance().textRenderer.fontHeight).toDouble())) { // hacky fix for size hacks
            textFieldWidget.mouseClicked(40.0 * 2 - 1.0, MinecraftClient.getInstance().textRenderer.fontHeight + 0.5, button)
            return true
        } else {
            textFieldWidget.mouseClicked(-1.0, -1.0, button)
            textFieldWidget.setCursorToEnd()
        }

        for ((index, key) in valueTextList.value.withIndex()) {
            if (RenderUtil.isHovered(mouseX, mouseY, (width.toFloat() - MinecraftClient.getInstance().textRenderer.getWidth(key) / 2.0F).toDouble(), (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * (index + 0.5F)).toDouble(), width, (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * ((index + 1) + 0.5F)).toDouble())) {
                valueTextList.value.remove(key)
                valueTextList.onChange()
                return true
            }
        }

        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double) = false

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (textFieldWidget.isFocused && (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
            val valueTextList = value as ValueTextList
            valueTextList.value.add(this.textFieldWidget.text)
            valueTextList.onChange()
            this.textFieldWidget.text = ""

            textFieldWidget.setTextFieldFocused(false)
            textFieldWidget.setCursorToEnd()
            return true
        } else {
            textFieldWidget.keyPressed(keyCode, scanCode, modifiers)
        }
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

    override fun getHeight(): Double {
        val valueTextList = value as ValueTextList
        return MinecraftClient.getInstance().textRenderer.fontHeight / 2.0 * (valueTextList.value.size + 1 + 1.5)
    }
}
