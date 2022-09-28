package de.florianmichael.tarasande.util.render

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

object RenderUtil {
    private val endStack = MatrixStack()
    private var globalStack = endStack

    fun scale(scaleFactor: Float) {
        globalStack.push()
        globalStack.scale(scaleFactor, scaleFactor, scaleFactor)
    }

    fun endPush() {
        globalStack.pop()
    }

    fun useMyStack(matrices: MatrixStack) {
        globalStack = matrices
    }

    fun ourStack() {
        globalStack = endStack
    }

    fun text(text: String, x: Float, y: Float) {
        text(Text.literal(text), x, y)
    }
    fun text(text: Text, x: Float, y: Float) = text(text, x, y, -1)
    fun text(text: Text, x: Float, y: Float, color: Int) {
        font().drawWithShadow(globalStack, text, x, y, color)
    }

    fun textCenter(text: String, x: Float, y: Float) {
        textCenter(Text.literal(text), x, y)
    }
    fun textCenter(text: Text, x: Float, y: Float) = textCenter(text, x, y, -1)
    fun textCenter(text: Text, x: Float, y: Float, color: Int) {
        font().drawWithShadow(globalStack, text, (x - font().getWidth(text) / 2), y, color)
    }

    private fun font(): TextRenderer {
        return MinecraftClient.getInstance().textRenderer
    }
}
