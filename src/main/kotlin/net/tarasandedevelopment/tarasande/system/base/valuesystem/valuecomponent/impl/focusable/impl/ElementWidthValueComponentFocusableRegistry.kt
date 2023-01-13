package net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.focusable.impl

import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.feature.clientvalue.ClientValues
import net.tarasandedevelopment.tarasande.injection.accessor.ITextFieldWidget
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.screen.widget.textfield.TextFieldWidgetPlaceholder
import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueRegistry
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.focusable.ElementWidthValueComponentFocusable
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import org.lwjgl.glfw.GLFW
import java.awt.Color
import java.util.concurrent.CopyOnWriteArrayList

class ElementWidthValueComponentFocusableRegistry(value: Value) : ElementWidthValueComponentFocusable(value) {
    //TODO
    private val textFieldWidget = TextFieldWidgetPlaceholder(mc.textRenderer, 0, 0, 40 * 2, FontWrapper.fontHeight() * 2 - 1, Text.of("Search"))
    private val textFieldAccessor = textFieldWidget as ITextFieldWidget

    private val searchResults = CopyOnWriteArrayList<ValueRegistry.WrappedKey<*>>()

    init {
        textFieldAccessor.tarasande_disableSelectionHighlight()
        textFieldWidget.setMaxLength(Int.MAX_VALUE)
        textFieldWidget.setDrawsBackground(false)
        textFieldWidget.setChangedListener { updateSearchResults() }
        updateSearchResults()
    }

    override fun init() {
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        val valueRegistry = value as ValueRegistry<*>
        val entries = valueRegistry.entries()

        val white = Color.white.let { if (valueRegistry.isEnabled()) it else it.darker().darker() }

        FontWrapper.textShadow(matrices, value.name, 0.0F, (getHeight() * 0.5F - FontWrapper.fontHeight() * 0.5F * 0.5F).toFloat(), Color.white.let { if (value.isEnabled()) it else it.darker().darker() }.rgb, scale = 0.5F, offset = 0.5F)

        for ((index, key) in entries.withIndex()) {
            val stringRepresentation = StringUtil.uncoverTranslation(valueRegistry.getTranslationKey(key))
            FontWrapper.textShadow(matrices,
                stringRepresentation,
                width.toFloat() - FontWrapper.getWidth(stringRepresentation) / 2.0F,
                FontWrapper.fontHeight() / 2.0F * (index + 0.5F),
                if (valueRegistry.isEnabled())
                    if (RenderUtil.isHovered(
                            mouseX.toDouble(),
                            mouseY.toDouble(),
                            (width.toFloat() - FontWrapper.getWidth(stringRepresentation) / 2.0F).toDouble(),
                            (FontWrapper.fontHeight() / 2.0F * (index + 0.5F)).toDouble(),
                            width,
                            (FontWrapper.fontHeight() / 2.0F * ((index + 1) + 0.5F)).toDouble()))
                        ClientValues.accentColor.getColor().rgb
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
        if (textFieldWidget.isFocused) textFieldAccessor.tarasande_setColor(ClientValues.accentColor.getColor())
        if (!value.isEnabled()) textFieldAccessor.tarasande_setColor(Color.white.darker().darker())
        textFieldWidget.render(matrices, mouseX, mouseY, delta)
        textFieldAccessor.tarasande_setColor(null)
        matrices.pop()

        for ((index, key) in searchResults.withIndex()) {
            FontWrapper.textShadow(matrices,
                key.string,
                width.toFloat() - FontWrapper.getWidth(key.string) / 2.0F,
                FontWrapper.fontHeight() / 2.0F * ((entries.size + 1) + index + 0.5F) + 2.0F,
                (if (valueRegistry.isEnabled())
                    if (RenderUtil.isHovered(
                            mouseX.toDouble(),
                            mouseY.toDouble(),
                            (width.toFloat() - FontWrapper.getWidth(key.string) / 2.0F).toDouble(),
                            (FontWrapper.fontHeight() / 2.0F * ((entries.size + 1) + index + 0.5F) + 2.0F).toDouble(),
                            width,
                            (FontWrapper.fontHeight() / 2.0F * ((entries.size + 1) + index + 0.5F) + 2.0F + FontWrapper.fontHeight() / 2.0F).toDouble()))
                        ClientValues.accentColor.getColor()
                    else
                        Color.white
                else
                    Color.white.darker().darker()).rgb,
                scale = 0.5F,
                offset = 0.5F
            )
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0) return false
        val valueRegistry = value as ValueRegistry<*>
        val entries = valueRegistry.entries()

        if (RenderUtil.isHovered(mouseX, mouseY, width - 40, FontWrapper.fontHeight() / 2.0F * (entries.size + 0.25) + 2.0, width, (FontWrapper.fontHeight() / 2.0F * entries.size + FontWrapper.fontHeight()).toDouble())) { // hacky fix for size hacks
            textFieldWidget.mouseClicked(40.0 * 2 - 1.0, FontWrapper.fontHeight() + 0.5, button)
            return true
        } else {
            textFieldWidget.mouseClicked(-1.0, -1.0, button)
            textFieldWidget.setCursorToEnd()
        }

        for ((index, key) in entries.withIndex()) {
            if (RenderUtil.isHovered(mouseX, mouseY, (width.toFloat() - FontWrapper.getWidth(StringUtil.uncoverTranslation(valueRegistry.getTranslationKey(key))) / 2.0F).toDouble(), (FontWrapper.fontHeight() / 2.0F * (index + 0.5F)).toDouble(), width, (FontWrapper.fontHeight() / 2.0F * ((index + 1) + 0.5F)).toDouble())) {
                valueRegistry.remove(key)
                updateSearchResults()
                return true
            }
        }

        for ((index, key) in searchResults.withIndex()) {
            if (RenderUtil.isHovered(mouseX, mouseY, (width.toFloat() - FontWrapper.getWidth(key.string) / 2.0F).toDouble(), (FontWrapper.fontHeight() / 2.0F * ((entries.size + 1) + index + 0.5F) + 2.0F).toDouble(), width, (FontWrapper.fontHeight() / 2.0F * ((entries.size + 1) + index + 0.5F) + 2.0F + FontWrapper.fontHeight() / 2.0F).toDouble())) {
                if (!valueRegistry.isSelected(key.key)) {
                    valueRegistry.add(key)
                    updateSearchResults()
                    return true
                }
            }
        }

        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double) = false

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return if (textFieldWidget.isFocused && (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
            if (searchResults.size == 1) {
                val valueRegistry = value as ValueRegistry<*>
                valueRegistry.add(searchResults[0])
                textFieldWidget.text = ""
                updateSearchResults()
            }
            textFieldWidget.setTextFieldFocused(false)
            textFieldWidget.setCursorToEnd()
            true
        } else {
            textFieldWidget.keyPressed(keyCode, scanCode, modifiers)
        }
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

    private fun updateSearchResults() {
        searchResults.clear()
        searchResults.addAll((value as ValueRegistry<*>).updateSearchResults(textFieldWidget.text, 4))
    }

    override fun isFocused() = textFieldWidget.isFocused

    override fun getHeight(): Double {
        val valueRegistry = value as ValueRegistry<*>
        return FontWrapper.fontHeight() / 2.0 * (valueRegistry.entries().size + 1 + searchResults.size + 1.5)
    }
}