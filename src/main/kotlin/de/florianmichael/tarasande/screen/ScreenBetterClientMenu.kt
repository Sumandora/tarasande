package de.florianmichael.tarasande.screen

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.util.render.screen.ScreenBetter

class ScreenBetterClientMenuHandler {

    private fun anySelected() = TarasandeMain.get().clientValues.focusedMenuEntry!!.anySelected() && TarasandeMain.get().clientValues.focusedMenuEntry!!.selected[0] != "None"

    fun buttonText(): Text {
        if (this.anySelected())
            return Text.literal(TarasandeMain.get().clientValues.focusedMenuEntry!!.selected[0])

        return Text.literal("Tarasande Menu")
    }

    fun doAction(parent: Screen) {
        if (this.anySelected()) {
            TarasandeMain.get().managerMenu.byName(TarasandeMain.get().clientValues.focusedMenuEntry!!.selected[0]).onClick()
            return
        }
        MinecraftClient.getInstance().setScreen(ScreenBetterClientMenu(parent))
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

        val endHeight = TarasandeMain.get().managerMenu.list.size * (buttonHeight + spacer)

        TarasandeMain.get().managerMenu.list.forEachIndexed { index, menu ->
            this.addDrawableChild(ButtonWidget(startX, this.halfHeight() - endHeight / 2 + (index * (buttonHeight + spacer)), buttonWidth, buttonHeight, Text.literal(menu.name)) {
                menu.onClick()
            })
        }
    }
}
