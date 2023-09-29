package su.mandora.tarasande.util.render.helper

import net.minecraft.client.gui.DrawContext

interface IElement {
    fun init()
    fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float)
    fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean
    fun mouseReleased(mouseX: Double, mouseY: Double, button: Int)
    fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean
    fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean
    fun charTyped(chr: Char, modifiers: Int)
    fun tick()
    fun onClose()
    fun getHeight(): Double
}