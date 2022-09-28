package de.florianmichael.tarasande.screen

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.screen.menu.valuecomponent.ValueComponent
import su.mandora.tarasande.screen.menu.panel.impl.elements.PanelElements
import su.mandora.tarasande.screen.widget.panel.ClickableWidgetPanel
import su.mandora.tarasande.util.render.screen.ScreenBetter

class ScreenBetterClientMenuHandler {

    private fun anySelected() = TarasandeMain.get().managerMenu.settings!!.focusedMenuEntry!!.anySelected() && TarasandeMain.get().managerMenu.settings!!.focusedMenuEntry!!.selected[0] != "None"

    fun buttonText(): Text {
        if (this.anySelected())
            return Text.literal(TarasandeMain.get().managerMenu.settings!!.focusedMenuEntry!!.selected[0])

        return Text.literal("Tarasande Menu")
    }

    fun doAction(parent: Screen) {
        if (this.anySelected()) {
            TarasandeMain.get().managerMenu.byName(TarasandeMain.get().managerMenu.settings!!.focusedMenuEntry!!.selected[0]).onClick()
            return
        }
        MinecraftClient.getInstance().setScreen(ScreenBetterClientMenu(parent))
    }
}

class ScreenBetterClientMenuSettings(parent: Screen) : ScreenBetter(parent) {

    override fun init() {
        super.init()
        this.addDrawableChild(ButtonWidget(5, this.height - 25, 20, 20, Text.literal("<-")) {
            close()
        })

        this.addDrawableChild(ClickableWidgetPanel(object : PanelElements<ValueComponent>("Settings", 0.0, 0.0, 0.0, 0.0) {

            override fun init() {
                for (it in TarasandeMain.get().managerValue.getValues(TarasandeMain.get().managerMenu.settings!!))
                    elementList.add(TarasandeMain.get().screenCheatMenuHandler.get().managerValueComponent.newInstance(it)!!)
                super.init()

                var height = titleBarHeight.toDouble()
                for (valueComponent in this.elementList)
                    height += valueComponent.getHeight() + 2

                this.panelWidth = 300.0
                this.panelHeight = height

                this.x = (MinecraftClient.getInstance().window.scaledWidth / 2) - 150.0
                this.y = MinecraftClient.getInstance().window.scaledHeight / 2 - (this.panelHeight / 2)
            }
        }))
    }
}

class ScreenBetterClientMenu(parent: Screen) : ScreenBetter(parent) {

    private val buttonWidth = 200
    private val buttonHeight = 20
    private val spacer = 4

    override fun init() {
        super.init()
        val startX = this.halfWidth() - (buttonWidth / 2)

        this.addDrawableChild(ButtonWidget(5, this.height - 25, 20, 20, Text.literal("<-")) {
            close()
        })
        this.addDrawableChild(ButtonWidget(5, 5, 98, 20, Text.literal("Settings")) {
            MinecraftClient.getInstance().setScreen(ScreenBetterClientMenuSettings(this))
        })

        val endHeight = TarasandeMain.get().managerMenu.list.size * (buttonHeight + spacer)

        TarasandeMain.get().managerMenu.list.forEachIndexed { index, menu ->
            this.addDrawableChild(menu.buildWidget(startX, this.halfHeight() - endHeight / 2 + (index * (buttonHeight + spacer)), buttonWidth, buttonHeight))
        }
    }
}
