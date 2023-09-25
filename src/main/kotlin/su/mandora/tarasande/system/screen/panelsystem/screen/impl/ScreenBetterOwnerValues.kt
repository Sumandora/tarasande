package su.mandora.tarasande.system.screen.panelsystem.screen.impl

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.ManagerValue
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import su.mandora.tarasande.system.screen.panelsystem.api.PanelElements

class ScreenBetterOwnerValues(title: String, parent: Screen, owner: Any) : ScreenBetterPanel(title, parent, object : PanelElements<ElementWidthValueComponent<*>>(title, 300.0, 0.0) {

    init {
        elementList.addAll(ManagerValue.getValues(owner).mapNotNull { it.createValueComponent() })
    }

    override fun init() {
        super.init()

        this.x = mc.window.scaledWidth / 2.0 - panelWidth / 2.0
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        panelHeight = getMaxScrollOffset() + titleBarHeight + 5.0 /* this is the padding for letting you scroll down a bit more than possible */
        panelHeight = panelHeight.coerceAtMost(mc.window.scaledHeight.toDouble())
        y = mc.window.scaledHeight / 2 - (this.panelHeight / 2)

        super.render(context, mouseX, mouseY, delta)
    }
})