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
import su.mandora.tarasande.value.ValueRegistry
import su.mandora.tarasande.value.WrappedKey
import java.awt.Color
import java.util.concurrent.CopyOnWriteArrayList

class ValueComponentRegistry(value: Value) : ValueComponent(value) {
    private val textFieldWidget = TextFieldWidgetPlaceholder(MinecraftClient.getInstance().textRenderer, 0, 0, 40 * 2, MinecraftClient.getInstance().textRenderer.fontHeight * 2 - 1, Text.of("Search"))

    private val searchResults = CopyOnWriteArrayList<WrappedKey<*>>()

    init {
        textFieldWidget.setMaxLength(Int.MAX_VALUE)
        textFieldWidget.setDrawsBackground(false)
        textFieldWidget.setChangedListener { updateSearchResults() }
        updateSearchResults()
    }

    override fun init() {
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val valueRegistry = value as ValueRegistry<*>

        matrices?.push()
        matrices?.translate(0.0, getHeight() / 2.0, 0.0)
        matrices?.scale(0.5F, 0.5F, 1.0F)
        matrices?.translate(0.0, -getHeight() / 2.0, 0.0)
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, value.name, 0.0F, (getHeight() / 2.0F - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F).toFloat(), Color.white.let { if (valueRegistry.isEnabled()) it else it.darker().darker() }.rgb)
        matrices?.pop()

        for ((index, key) in valueRegistry.list.withIndex()) {
            matrices?.push()
            matrices?.scale(0.5F, 0.5F, 1.0F)
            val stringRepresentation = valueRegistry.keyToString(key)
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, stringRepresentation, (width.toFloat() - MinecraftClient.getInstance().textRenderer.getWidth(stringRepresentation) / 2.0F) * 2.0F, MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * (index + 0.5F) * 2.0F, if (valueRegistry.isEnabled()) if (RenderUtil.isHovered(mouseX.toDouble(), mouseY.toDouble(), (width.toFloat() - MinecraftClient.getInstance().textRenderer.getWidth(stringRepresentation) / 2.0F).toDouble(), (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * (index + 0.5F)).toDouble(), width, (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * ((index + 1) + 0.5F)).toDouble())) TarasandeMain.get().clientValues?.accentColor?.getColor()?.rgb!! else Color.white.rgb else Color.white.darker().darker().rgb)
            matrices?.pop()
        }
        RenderUtil.fill(matrices, width.toFloat() - 25.0, (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * (valueRegistry.list.size + 0.5F)).toDouble() + 1.0, width, (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * (valueRegistry.list.size + 0.5F)).toDouble() + 1.5, Color.white.let { if (valueRegistry.isEnabled()) it else it.darker().darker() }.rgb)

        matrices?.push()
        matrices?.translate(width - 40, MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * (valueRegistry.list.size + 0.5F) + 2.0, 0.0)
        matrices?.scale(0.5F, 0.5F, 1.0F)
        if (textFieldWidget.isFocused)
            (textFieldWidget as ITextFieldWidget).setColor(TarasandeMain.get().clientValues?.accentColor?.getColor()!!)
        if (!value.isEnabled())
            (textFieldWidget as ITextFieldWidget).setColor(Color.white.darker().darker())
        textFieldWidget.render(matrices, mouseX, mouseY, delta)
        (textFieldWidget as ITextFieldWidget).setColor(null)
        matrices?.pop()

        for ((index, key) in searchResults.withIndex()) {
            matrices?.push()
            matrices?.scale(0.5F, 0.5F, 1.0F)
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, key.string, (width.toFloat() - MinecraftClient.getInstance().textRenderer.getWidth(key.string) / 2.0F) * 2.0F, (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * ((valueRegistry.list.size + 1) + index + 0.5F) + 2.0F) * 2.0F, if (valueRegistry.isEnabled()) if (RenderUtil.isHovered(mouseX.toDouble(), mouseY.toDouble(), (width.toFloat() - MinecraftClient.getInstance().textRenderer.getWidth(key.string) / 2.0F).toDouble(), (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * ((valueRegistry.list.size + 1) + index + 0.5F) + 2.0F).toDouble(), width, (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * ((valueRegistry.list.size + 1) + index + 0.5F) + 2.0F + MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F).toDouble())) TarasandeMain.get().clientValues?.accentColor?.getColor()?.rgb!! else Color.white.rgb else Color.white.darker().darker().rgb)
            matrices?.pop()
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0) return false
        val valueRegistry = value as ValueRegistry<*>

        if (RenderUtil.isHovered(mouseX, mouseY, width - 40, MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * (valueRegistry.list.size + 0.25) + 2.0, width, (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * valueRegistry.list.size + MinecraftClient.getInstance().textRenderer.fontHeight).toDouble())) { // hacky fix for size hacks
            textFieldWidget.mouseClicked(40.0 * 2 - 1.0, MinecraftClient.getInstance().textRenderer.fontHeight + 0.5, button)
            return true
        } else {
            textFieldWidget.mouseClicked(-1.0, -1.0, button)
            textFieldWidget.setCursorToEnd()
        }

        for ((index, key) in valueRegistry.list.withIndex()) {
            if (RenderUtil.isHovered(mouseX, mouseY, (width.toFloat() - MinecraftClient.getInstance().textRenderer.getWidth(valueRegistry.keyToString(key)) / 2.0F).toDouble(), (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * (index + 0.5F)).toDouble(), width, (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * ((index + 1) + 0.5F)).toDouble())) {
                valueRegistry.list.remove(key)
                valueRegistry.onChange()
                updateSearchResults()
                return true
            }
        }

        for ((index, key) in searchResults.withIndex()) {
            if (RenderUtil.isHovered(mouseX, mouseY, (width.toFloat() - MinecraftClient.getInstance().textRenderer.getWidth(key.string) / 2.0F).toDouble(), (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * ((valueRegistry.list.size + 1) + index + 0.5F) + 2.0F).toDouble(), width, (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * ((valueRegistry.list.size + 1) + index + 0.5F) + 2.0F + MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F).toDouble())) {
                if (!valueRegistry.list.contains(key.key)) {
                    valueRegistry.add(key)
                    valueRegistry.onChange()
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
        if (textFieldWidget.isFocused && (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
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

    private fun updateSearchResults() {
        searchResults.clear()
        searchResults.addAll((value as ValueRegistry<*>).updateSearchResults(textFieldWidget.text, 4))
    }

    fun isFocused() = textFieldWidget.isFocused

    override fun getHeight(): Double {
        val valueRegistry = value as ValueRegistry<*>
        return MinecraftClient.getInstance().textRenderer.fontHeight / 2.0 * (valueRegistry.list.size + 1 + searchResults.size + 1.5)
    }
}