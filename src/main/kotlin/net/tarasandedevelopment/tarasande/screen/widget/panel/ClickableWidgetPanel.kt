package net.tarasandedevelopment.tarasande.screen.widget.panel

import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.Panel

open class ClickableWidgetPanel(val panel: Panel) : ClickableWidget(panel.x.toInt(), panel.y.toInt(), panel.panelWidth.toInt(), panel.panelWidth.toInt(), Text.of(panel.title)), Element {

    override fun appendNarrations(builder: NarrationMessageBuilder?) {
    }

    init {
        panel.init()
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val opened = panel.opened
        panel.opened = true
        panel.x = x.toDouble()
        panel.y = y.toDouble()
        panel.panelWidth = width.toDouble()
        panel.panelHeight = height.toDouble()
        if (panel.isVisible())
            panel.render(matrices, mouseX, mouseY, delta)
        panel.opened = opened
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        panel.modifiable = false
        val returnType = panel.isVisible() && panel.mouseClicked(mouseX, mouseY, button)
        panel.modifiable = true
        return returnType
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (panel.isVisible())
            panel.mouseReleased(mouseX, mouseY, button)
        return true
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        return panel.isVisible() && panel.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return panel.isVisible() && panel.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        if (panel.isVisible())
            panel.charTyped(chr, modifiers)
        return false
    }

    fun tick() = panel.tick()

}