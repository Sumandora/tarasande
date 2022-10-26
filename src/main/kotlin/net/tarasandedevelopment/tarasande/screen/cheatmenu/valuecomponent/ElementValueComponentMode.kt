package net.tarasandedevelopment.tarasande.screen.cheatmenu.valuecomponent

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.valuecomponent.ElementValueComponent
import net.tarasandedevelopment.tarasande.base.value.Value
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.value.ValueMode
import java.awt.Color

class ElementValueComponentMode(value: Value) : ElementValueComponent(value) {

    override fun init() {
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val valueMode = value as ValueMode

        matrices?.push()
        matrices?.translate(0.0, getHeight() / 2.0, 0.0)
        matrices?.scale(0.5F, 0.5F, 1.0F)
        matrices?.translate(0.0, -getHeight() / 2.0, 0.0)
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, value.name, 0.0F, (getHeight() / 2.0F - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F).toFloat(), Color.white.let { if (valueMode.isEnabled()) it else it.darker().darker() }.rgb)
        matrices?.pop()

        matrices?.push()
        matrices?.scale(0.5F, 0.5F, 1.0F)
        for ((index, setting) in valueMode.settings.withIndex()) {
            var color = if (valueMode.selected.contains(setting)) TarasandeMain.get().clientValues.accentColor.getColor() else Color.white
            if (!valueMode.isEnabled()) color = color.darker().darker()
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, setting, (width * 2.0f - MinecraftClient.getInstance().textRenderer.getWidth(setting)).toFloat(), (getHeight() / 2.0F + (index - (valueMode.settings.size - 1) / 2.0 + 0.5) * (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0f) - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F).toFloat() * 2.0f, color.rgb)
        }
        matrices?.pop()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0) return false
        val valueMode = value as ValueMode
        for ((index, setting) in valueMode.settings.withIndex()) {
            val x = width - MinecraftClient.getInstance().textRenderer.getWidth(setting) / 2.0f
            val y = getHeight() / 2.0F + (index - (valueMode.settings.size - 1) / 2.0 + 0.5) * (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0f) - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F
            if (RenderUtil.isHovered(mouseX, mouseY, x, y, width, y + MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F)) {
                valueMode.select(index)
                valueMode.onChange()
                return true
            }
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double) = false

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int) = false

    override fun charTyped(chr: Char, modifiers: Int) {
    }

    override fun tick() {
    }

    override fun onClose() {
    }

    override fun getHeight() = ((value as ValueMode).settings.size + 1) * MinecraftClient.getInstance().textRenderer.fontHeight.toDouble() / 2
}