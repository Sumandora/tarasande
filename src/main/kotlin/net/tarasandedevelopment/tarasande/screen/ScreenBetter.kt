package net.tarasandedevelopment.tarasande.screen

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.screen.menu.panel.impl.elements.impl.notification.PanelElementsNotification
import net.tarasandedevelopment.tarasande.screen.widget.panel.ClickableWidgetPanel

open class ScreenBetter(internal var prevScreen: Screen?) : Screen(Text.of("")) {

    private lateinit var clickableWidgetPanel: ClickableWidgetPanel

    override fun init() {
        super.init()

        this.addDrawableChild(object : ClickableWidgetPanel(TarasandeMain.get().screenCheatMenu.panels.filterIsInstance<PanelElementsNotification>().first()) {

            override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
                val oldX = this.panel.x
                val oldY = this.panel.y

                panel.x = MinecraftClient.getInstance().window.scaledWidth - panel.panelWidth - 5
                panel.y = 5.0
                super.render(matrices, mouseX, mouseY, delta)
                panel.x = oldX
                panel.y = oldY
            }

            override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
                val oldX = this.panel.x
                val oldY = this.panel.y

                panel.x = MinecraftClient.getInstance().window.scaledWidth - panel.panelWidth - 5
                panel.y = 5.0
                val returnType = super.mouseClicked(mouseX, mouseY, button)
                panel.x = oldX
                panel.y = oldY

                return returnType
            }

            override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
                val oldX = this.panel.x
                val oldY = this.panel.y

                panel.x = MinecraftClient.getInstance().window.scaledWidth - panel.panelWidth - 5
                panel.y = 5.0
                val returnType = super.mouseScrolled(mouseX, mouseY, amount)
                panel.x = oldX
                panel.y = oldY

                return returnType
            }
        }.also { clickableWidgetPanel = it })
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

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        clickableWidgetPanel.mouseScrolled(mouseX, mouseY, amount)
        return super.mouseScrolled(mouseX, mouseY, amount)
    }
}