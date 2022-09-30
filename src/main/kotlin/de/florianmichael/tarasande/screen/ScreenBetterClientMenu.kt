package de.florianmichael.tarasande.screen

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.util.render.screen.ScreenBetter

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
            MinecraftClient.getInstance().setScreen(ScreenBetterParentPopupSettings(this, "Menu Settings", TarasandeMain.get().managerMenu.settings!!))
        })

        val endHeight = TarasandeMain.get().managerMenu.list.size * (buttonHeight + spacer)

        TarasandeMain.get().managerMenu.list.forEachIndexed { index, menu ->
            this.addDrawableChild(menu.buildWidget(startX, this.halfHeight() - endHeight / 2 + (index * (buttonHeight + spacer)), buttonWidth, buttonHeight))
        }
    }
}
