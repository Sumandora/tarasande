package net.tarasandedevelopment.tarasande.screen.cheatmenu.utils

import net.minecraft.client.util.math.MatrixStack

interface IElement {
    fun init()
    fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float)
    fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean
    fun mouseReleased(mouseX: Double, mouseY: Double, button: Int)
    fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean
    fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean
    fun charTyped(chr: Char, modifiers: Int)
    fun tick()
    fun onClose()
    fun getHeight(): Double
}