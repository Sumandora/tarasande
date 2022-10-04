package net.tarasandedevelopment.tarasande.util.render.screen

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.screen.menu.panel.impl.elements.impl.notification.PanelElementsNotification
import net.tarasandedevelopment.tarasande.screen.widget.panel.ClickableWidgetPanel

open class ScreenBetter(internal var prevScreen: Screen?) : Screen(Text.of("")) {

    override fun init() {
        super.init()

        this.addDrawableChild(object : ClickableWidgetPanel(TarasandeMain.get().screenCheatMenu.panels.filterIsInstance<PanelElementsNotification>().first()) {
            init {
                panel.x = MinecraftClient.getInstance().window.scaledWidth - panel.panelWidth - 5
                panel.y = 5.0
            }
        })
    }

    override fun close() {
        client!!.setScreen(prevScreen)
    }

    fun halfWidth(): Int {
        return this.width / 2
    }

    fun halfHeight(): Int {
        return this.height / 2
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, delta)
    }
}