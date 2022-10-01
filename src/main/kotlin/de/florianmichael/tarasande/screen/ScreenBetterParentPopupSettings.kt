package de.florianmichael.tarasande.screen

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.screen.menu.valuecomponent.ValueComponent
import su.mandora.tarasande.screen.menu.panel.impl.elements.PanelElements
import su.mandora.tarasande.screen.widget.panel.ClickableWidgetPanel
import su.mandora.tarasande.util.render.screen.ScreenBetter

class ScreenBetterParentPopupSettings(parent: Screen, val titleName: String, val owner: Any) : ScreenBetter(parent) {

    private lateinit var clickableWidgetPanel: ClickableWidgetPanel

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

                var height = titleBarHeight.toDouble()
                for (valueComponent in this.elementList)
                    height += valueComponent.getHeight() + 2

                this.panelWidth = 300.0
                this.panelHeight = height

                this.x = (MinecraftClient.getInstance().window.scaledWidth / 2) - 150.0
                this.y = MinecraftClient.getInstance().window.scaledHeight / 2 - (this.panelHeight / 2)
            }
        }).also { clickableWidgetPanel = it })
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(matrices)
        if (MinecraftClient.getInstance().world != null)
            this.prevScreen!!.render(matrices, mouseX, mouseY, delta)

        super.render(matrices, mouseX, mouseY, delta)
    }
}
