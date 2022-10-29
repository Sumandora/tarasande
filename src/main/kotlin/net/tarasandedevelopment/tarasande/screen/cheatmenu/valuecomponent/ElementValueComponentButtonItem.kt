package net.tarasandedevelopment.tarasande.screen.cheatmenu.valuecomponent

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.valuecomponent.ElementValueComponent
import net.tarasandedevelopment.tarasande.base.value.Value
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.value.meta.ValueButtonItem
import java.awt.Color

class ElementValueComponentButtonItem(value: Value) : ElementValueComponent(value) {

    override fun init() {
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val valueButton = value as ValueButtonItem

        val textWidth = MinecraftClient.getInstance().textRenderer.getWidth(valueButton.name)

        RenderUtil.fill(matrices, width - 4 - textWidth / 2, getHeight() / 2.0 - MinecraftClient.getInstance().textRenderer.fontHeight / 2, width, getHeight() / 2.0 + MinecraftClient.getInstance().textRenderer.fontHeight / 2, Int.MIN_VALUE)

        RenderSystem.enableCull()
        RenderUtil.renderCorrectItem(matrices!!, 0, 0, delta, valueButton.icon)

        matrices.push()
        matrices.translate(width - 2 - textWidth / 2, getHeight() / 2.0, 0.0)
        matrices.scale(0.5F, 0.5F, 1.0F)
        matrices.translate(-(width - 2 - textWidth / 2), -getHeight() / 2.0, 0.0)
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, value.name, (width - 2 - textWidth / 2).toFloat(), (getHeight() / 2.0F - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F + 1).toFloat(), if (valueButton.isEnabled()) if (RenderUtil.isHovered(mouseX.toDouble(), mouseY.toDouble(), width - 4 - textWidth / 2, getHeight() / 2.0 - MinecraftClient.getInstance().textRenderer.fontHeight / 2, width, getHeight() / 2.0 + MinecraftClient.getInstance().textRenderer.fontHeight / 2)) TarasandeMain.get().clientValues.accentColor.getColor().rgb else -1 else Color.white.darker().darker().rgb)
        matrices.pop()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val valueButton = value as ValueButtonItem

        val textWidth = MinecraftClient.getInstance().textRenderer.getWidth(valueButton.name)

        if (button == 0 && RenderUtil.isHovered(mouseX, mouseY, width - 4 - textWidth / 2, getHeight() / 2.0 - MinecraftClient.getInstance().textRenderer.fontHeight / 2, width, getHeight() / 2.0 + MinecraftClient.getInstance().textRenderer.fontHeight / 2)) {
            valueButton.onChange()
            return true
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

    override fun getHeight() = MinecraftClient.getInstance().textRenderer.fontHeight * 1.5
}