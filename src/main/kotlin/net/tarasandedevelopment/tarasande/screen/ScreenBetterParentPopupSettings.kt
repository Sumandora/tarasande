package net.tarasandedevelopment.tarasande.screen

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.menu.valuecomponent.ValueComponent
import net.tarasandedevelopment.tarasande.screen.menu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.menu.panel.impl.elements.PanelElements
import net.tarasandedevelopment.tarasande.screen.widget.panel.ClickableWidgetPanel
import net.tarasandedevelopment.tarasande.util.render.screen.ScreenBetter

class ScreenBetterParentPopupSettings(parent: Screen, val titleName: String, val owner: Any) : ScreenBetter(parent) {

    private lateinit var clickableWidgetPanel: ClickableWidgetPanel

    init {
        if (parent is ScreenCheatMenu)
            parent.popup = true
    }

    override fun init() {
        super.init()
        this.addDrawableChild(ButtonWidget(5, this.height - 25, 20, 20, Text.literal("<-")) {
            close()
        })

        this.addDrawableChild(ClickableWidgetPanel(object : PanelElements<ValueComponent>(this.titleName, 0.0, 0.0, 0.0, 0.0) {

            override fun init() {
                for (it in TarasandeMain.get().managerValue.getValues(owner))
                    elementList.add(TarasandeMain.get().screenCheatMenu.managerValueComponent.newInstance(it)!!)
                super.init()

                var height = titleBarHeight.toDouble() + 2
                for (valueComponent in this.elementList)
                    height += valueComponent.getHeight() + 2

                this.panelWidth = 300.0
                this.panelHeight = height
                val max = MinecraftClient.getInstance().window.scaledHeight
                if (this.panelHeight >= max)
                    this.panelHeight = max.toDouble()

                this.x = (MinecraftClient.getInstance().window.scaledWidth / 2) - 150.0
                this.y = MinecraftClient.getInstance().window.scaledHeight / 2 - (this.panelHeight / 2)
            }
        }).also { clickableWidgetPanel = it })
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        this.clickableWidgetPanel.mouseReleased(mouseX, mouseY, button)
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        this.clickableWidgetPanel.mouseScrolled(mouseX, mouseY, amount)
        return super.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(matrices)
        if (MinecraftClient.getInstance().world != null)
            this.prevScreen!!.render(matrices, -1, -1, delta)

        super.render(matrices, mouseX, mouseY, delta)
    }
}
