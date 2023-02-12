package net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.focusable.impl

import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.feature.tarasandevalue.TarasandeValues
import net.tarasandedevelopment.tarasande.injection.accessor.ITextFieldWidget
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueTextList
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.focusable.ElementWidthValueComponentFocusable
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import net.tarasandedevelopment.tarasande.util.screen.widget.TextFieldWidgetPlaceholder
import org.lwjgl.glfw.GLFW
import java.awt.Color

class ElementWidthValueComponentFocusableTextList(value: Value) : ElementWidthValueComponentFocusable<ValueTextList>(value) {
    //TODO
    private val textFieldWidget = TextFieldWidgetPlaceholder(mc.textRenderer, 0, 0, 40 * 2, FontWrapper.fontHeight() * 2 - 1, Text.of("Input text"))
    private val textFieldAccessor = textFieldWidget as ITextFieldWidget

    init {
        textFieldAccessor.tarasande_disableSelectionHighlight()
        textFieldWidget.setMaxLength(Int.MAX_VALUE)
        textFieldWidget.setDrawsBackground(false)
    }

    override fun init() {
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        val entries = value.entries()

        val white = Color.white.let { if (value.isEnabled()) it else it.darker().darker() }

        FontWrapper.textShadow(matrices, value.name, 0.0F, (getHeight() * 0.5F - FontWrapper.fontHeight() * 0.5F * 0.5F).toFloat(), Color.white.let { if (value.isEnabled()) it else it.darker().darker() }.rgb, scale = 0.5F, offset = 0.5F)

        for ((index, key) in entries.withIndex()) {
            FontWrapper.textShadow(matrices,
                key,
                (width.toFloat() - FontWrapper.getWidth(key) / 2.0F),
                FontWrapper.fontHeight() / 2.0F * (index + 0.5F),
                if (value.isEnabled())
                    if (RenderUtil.isHovered(
                            mouseX.toDouble(),
                            mouseY.toDouble(),
                            (width.toFloat() - FontWrapper.getWidth(key) / 2.0F).toDouble(),
                            (FontWrapper.fontHeight() / 2.0F * (index + 0.5F)).toDouble(),
                            width,
                            (FontWrapper.fontHeight() / 2.0F * ((index + 1) + 0.5F)).toDouble()))
                        TarasandeValues.accentColor.getColor().rgb
                    else
                        -1
                else
                    Color.white.darker().darker().rgb,
                scale = 0.5F,
                offset = 0.5F
            )
        }
        RenderUtil.fill(matrices, width.toFloat() - 25.0, (FontWrapper.fontHeight() / 2.0F * (entries.size + 0.5F)).toDouble() + 1.0, width, (FontWrapper.fontHeight() / 2.0F * (entries.size + 0.5F)).toDouble() + 1.5, white.rgb)

        matrices.push()
        matrices.translate(width - 40, FontWrapper.fontHeight() / 2.0F * (entries.size + 0.5F) + 2.0, 0.0)
        matrices.scale(0.5F, 0.5F, 1.0F)
        if (textFieldWidget.isFocused) textFieldAccessor.tarasande_setColor(TarasandeValues.accentColor.getColor())
        if (!value.isEnabled()) textFieldAccessor.tarasande_setColor(Color.white.darker().darker())
        textFieldWidget.render(matrices, mouseX, mouseY, delta)
        textFieldAccessor.tarasande_setColor(null)
        matrices.pop()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0) return false
        val entries = value.entries()

        if (RenderUtil.isHovered(mouseX, mouseY, width - 40, FontWrapper.fontHeight() / 2.0F * (entries.size + 0.25) + 2.0, width, (FontWrapper.fontHeight() / 2.0F * entries.size + FontWrapper.fontHeight()).toDouble())) { // hacky fix for size hacks
            textFieldWidget.mouseClicked(40.0 * 2 - 1.0, FontWrapper.fontHeight() + 0.5, button)
            return true
        } else {
            textFieldWidget.mouseClicked(-1.0, -1.0, button)
            textFieldWidget.setCursorToEnd()
        }

        for ((index, key) in entries.withIndex()) {
            if (RenderUtil.isHovered(mouseX, mouseY, (width.toFloat() - FontWrapper.getWidth(key) / 2.0F).toDouble(), (FontWrapper.fontHeight() / 2.0F * (index + 0.5F)).toDouble(), width, (FontWrapper.fontHeight() / 2.0F * ((index + 1) + 0.5F)).toDouble())) {
                value.remove(key)
                return true
            }
        }

        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double) = false

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return if (textFieldWidget.isFocused && (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
            value.add(this.textFieldWidget.text)
            this.textFieldWidget.text = ""

            textFieldWidget.setTextFieldFocused(false)
            textFieldWidget.setCursorToEnd()
            true
        } else {
            textFieldWidget.keyPressed(keyCode, scanCode, modifiers)
        }
    }

    override fun charTyped(chr: Char, modifiers: Int) {
        if (textFieldWidget.isFocused) {
            textFieldWidget.charTyped(chr, modifiers)
        }
    }

    override fun tick() {
    }

    override fun onClose() {
        textFieldWidget.setTextFieldFocused(false)
        textFieldWidget.setCursorToEnd()
    }

    override fun isFocused() = textFieldWidget.isFocused

    override fun getHeight(): Double {
        return FontWrapper.fontHeight() / 2.0 * (value.entries().size + 1 + 1.5)
    }
}
