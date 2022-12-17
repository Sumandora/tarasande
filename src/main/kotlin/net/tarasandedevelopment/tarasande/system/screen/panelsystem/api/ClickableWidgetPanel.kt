package net.tarasandedevelopment.tarasande.system.screen.panelsystem.api

import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.Panel
import kotlin.math.floor

open class ClickableWidgetPanel(val panel: Panel, private val update: Boolean = false) : ClickableWidget(panel.x.toInt(), panel.y.toInt(), panel.panelWidth.toInt(), panel.panelHeight.toInt(), Text.of(panel.title)), Element {

    override fun appendClickableNarrations(builder: NarrationMessageBuilder?) {
    }

    init {
        panel.init()
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        val opened = panel.opened
        panel.opened = true
        val usedInScreen = panel.usedInScreen
        panel.usedInScreen = true
        if (update) {
            panel.x = this.x.toDouble()
            panel.y = this.y.toDouble()
            panel.panelWidth = this.width.toDouble()
            panel.panelHeight = this.height.toDouble()
        }
        if (panel.isVisible())
            panel.render(matrices, mouseX, mouseY, delta)
        panel.opened = opened
        panel.usedInScreen = usedInScreen
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        panel.modifiable = false
        val returnValue = panel.isVisible() && panel.mouseClicked(floor(mouseX), floor(mouseY), button)
        panel.modifiable = true
        return returnValue
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (panel.isVisible())
            panel.mouseReleased(mouseX, mouseY, button)
        return true
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        val opened = panel.opened
        panel.opened = true
        val returnValue = panel.isVisible() && panel.mouseScrolled(mouseX, mouseY, amount)
        panel.opened = opened
        return returnValue
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return panel.isVisible() && panel.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        if (panel.isVisible())
            panel.charTyped(chr, modifiers)
        return false
    }

    open fun tick() = panel.tick()

    override fun isFocused() = true

    override fun isHovered() = true

}