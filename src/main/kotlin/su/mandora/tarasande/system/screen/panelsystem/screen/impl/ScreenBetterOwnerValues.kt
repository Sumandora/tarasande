package su.mandora.tarasande.system.screen.panelsystem.screen.impl

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.Screen
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.ManagerValue
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import su.mandora.tarasande.system.screen.panelsystem.api.ClickableWidgetPanel
import su.mandora.tarasande.system.screen.panelsystem.api.PanelElements
import su.mandora.tarasande.util.screen.ScreenBetter
import java.util.*

class ScreenBetterOwnerValues(title: String, parent: Screen, val owner: Any) : ScreenBetter(title, parent) {

    private var clickableWidgetPanel: ClickableWidgetPanel? = null
    lateinit var panel: PanelElements<ElementWidthValueComponent<*>>

    override fun init() {
        super.init()
        this.addDrawableChild(ClickableWidgetPanel(object : PanelElements<ElementWidthValueComponent<*>>(this.title.string, 300.0, 0.0) {

            init {
                elementList.addAll(ManagerValue.getValues(owner).mapNotNull { it.createValueComponent() })
            }

            override fun init() {
                super.init()

                this.x = (mc.window.scaledWidth / 2) - 150.0
            }

            override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
                this.panelHeight = getMaxScrollOffset() + titleBarHeight + 5.0 /* this is the padding for letting you scroll down a bit more than possible */
                val max = mc.window.scaledHeight
                if (this.panelHeight >= max)
                    this.panelHeight = max.toDouble()
                this.y = mc.window.scaledHeight / 2 - (this.panelHeight / 2)

                super.render(context, mouseX, mouseY, delta)
            }
        }.also { panel = it }).also { clickableWidgetPanel = it })
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        this.clickableWidgetPanel?.mouseReleased(mouseX, mouseY, button)
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        this.clickableWidgetPanel?.mouseScrolled(mouseX, mouseY, amount)
        return super.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (mc.world != null && prevScreen != null) {
            var prevScreen = prevScreen
            while (prevScreen is ScreenBetterOwnerValues)
                prevScreen = prevScreen.prevScreen
            prevScreen!!.render(context, -1, -1, delta)
        }

        super.render(context, mouseX, mouseY, delta)
    }

    override fun hoveredElement(mouseX: Double, mouseY: Double): Optional<Element> {
        return Optional.of(clickableWidgetPanel ?: return Optional.empty())
    }

    override fun getFocused(): Element? {
        return clickableWidgetPanel
    }

    // Ultra 1337 hack because minecraft doesn't send escape key presses through to the widgets, means we have to ask our widgets here
    override fun close() {
        if (clickableWidgetPanel?.keyPressed(GLFW.GLFW_KEY_ESCAPE, 0, 0) == false)
            super.close()
    }

}
