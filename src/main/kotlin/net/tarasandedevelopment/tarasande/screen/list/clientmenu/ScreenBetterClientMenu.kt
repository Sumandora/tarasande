package net.tarasandedevelopment.tarasande.screen.list.clientmenu

import net.tarasandedevelopment.tarasande.screen.ScreenBetterParentPopupSettings
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.util.render.screen.ScreenBetter

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
            MinecraftClient.getInstance().setScreen(ScreenBetterParentPopupSettings(this, "Menu Settings", TarasandeMain.get().managerClientMenu.settings!!))
        })

        val endHeight = TarasandeMain.get().managerClientMenu.list.size * (buttonHeight + spacer)

        TarasandeMain.get().managerClientMenu.list.forEachIndexed { index, menu ->
            this.addDrawableChild(menu.buildWidget(startX, this.halfHeight() - endHeight / 2 + (index * (buttonHeight + spacer)), buttonWidth, buttonHeight))
        }
    }
}
