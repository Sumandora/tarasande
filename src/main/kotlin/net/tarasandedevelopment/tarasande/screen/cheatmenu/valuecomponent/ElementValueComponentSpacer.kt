package net.tarasandedevelopment.tarasande.screen.cheatmenu.valuecomponent

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.valuecomponent.ElementValueComponent
import net.tarasandedevelopment.tarasande.base.value.Value
import net.tarasandedevelopment.tarasande.value.ValueSpacer
import kotlin.math.floor

class ElementValueComponentSpacer(value: Value) : ElementValueComponent(value) {

    private val lines = ArrayList<String>()

    override fun init() {
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        lines.clear()
        var str = (value as ValueSpacer).name
        while (str.isNotEmpty()) {
            var trimmed = MinecraftClient.getInstance().textRenderer.trimToWidth(str, floor(width * 2.0f).toInt())
            if (trimmed != str) {
                val orig = trimmed
                while (trimmed.isNotEmpty() && !trimmed.endsWith(" ")) {
                    trimmed = trimmed.substring(0, trimmed.length - 1)
                }
                if (trimmed.isEmpty())
                    trimmed = orig
            }
            lines.add(trimmed)
            str = str.substring(trimmed.length)
        }

        matrices?.push()
        matrices?.scale(0.5F, 0.5F, 1.0F)

        for ((index, line) in lines.withIndex()) {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices,
                line,
                0.0f,
                (getHeight() / 2.0F + (index - (lines.size - 1) / 2.0 + 0.5) * (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0f) - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F).toFloat() * 2.0f,
                -1)
        }

        matrices?.pop()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int) = false

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

    override fun getHeight() = MinecraftClient.getInstance().textRenderer.fontHeight.toDouble() / 2.0f * (lines.size + 1)
}