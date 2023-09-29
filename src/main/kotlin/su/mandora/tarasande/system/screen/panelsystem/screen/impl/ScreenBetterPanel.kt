package su.mandora.tarasande.system.screen.panelsystem.screen.impl

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.Screen
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.feature.screen.ScreenBetter
import su.mandora.tarasande.system.screen.panelsystem.Panel
import su.mandora.tarasande.system.screen.panelsystem.api.ClickableWidgetPanel
import java.util.*

open class ScreenBetterPanel(title: String, parent: Screen, val panel: Panel) : ScreenBetter(title, parent) {

    private lateinit var clickableWidgetPanel: ClickableWidgetPanel

    override fun init() {
        super.init()
        this.addDrawableChild(ClickableWidgetPanel(panel).also { clickableWidgetPanel = it })
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        this.clickableWidgetPanel.mouseReleased(mouseX, mouseY, button)
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
        this.clickableWidgetPanel.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
    }

    override fun hoveredElement(mouseX: Double, mouseY: Double): Optional<Element> {
        return Optional.of(clickableWidgetPanel)
    }

    override fun getFocused(): Element {
        return clickableWidgetPanel
    }

    // Ultra 1337 hack because minecraft doesn't send escape key presses through to the widgets, means we have to ask our widgets here
    override fun close() {
        if (!clickableWidgetPanel.keyPressed(GLFW.GLFW_KEY_ESCAPE, 0, 0))
            super.close()
    }

    override fun shouldPause() = false

}
