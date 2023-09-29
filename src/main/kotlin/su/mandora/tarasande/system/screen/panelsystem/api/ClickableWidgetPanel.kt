package su.mandora.tarasande.system.screen.panelsystem.api

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.screen.narration.NarrationPart
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.text.Text
import su.mandora.tarasande.system.screen.panelsystem.Panel
import kotlin.math.floor

open class ClickableWidgetPanel(val panel: Panel, private val update: Boolean = false) : ClickableWidget(panel.x.toInt(), panel.y.toInt(), panel.panelWidth.toInt(), panel.panelHeight.toInt(), Text.of(panel.title)), Element {

    override fun appendClickableNarrations(builder: NarrationMessageBuilder?) {
        builder?.put(NarrationPart.TITLE, panel.title)
    }

    init {
        panel.init()
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

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
        val opened = panel.opened
        panel.opened = true
        val returnValue = panel.isVisible() && panel.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
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

    override fun renderButton(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val opened = panel.opened
        panel.opened = true
        if (update) {
            panel.x = this.x.toDouble()
            panel.y = this.y.toDouble()
            panel.panelWidth = this.width.toDouble()
            panel.panelHeight = this.height.toDouble()
        }
        if (panel.isVisible()) {
            panel.blurBackground(context, insideScreen = true)
            panel.render(context, mouseX, mouseY, delta)
        }
        panel.opened = opened
    }

    override fun isHovered() = true

}