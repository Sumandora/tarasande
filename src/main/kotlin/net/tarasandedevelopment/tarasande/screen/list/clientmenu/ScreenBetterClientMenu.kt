package net.tarasandedevelopment.tarasande.screen.list.clientmenu

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.clientmenu.ElementMenuTitle
import net.tarasandedevelopment.tarasande.screen.ScreenBetterParentPopupSettings
import net.tarasandedevelopment.tarasande.util.render.screen.ScreenBetter

class ScreenBetterClientMenu(parent: Screen) : ScreenBetter(parent) {

    private val buttonWidth = 200
    private val buttonHeight = 20
    private val spacer = 4

    override fun init() {
        super.init()
        val startX = this.halfWidth() - (buttonWidth / 2)

        this.addDrawableChild(ButtonWidget(5, this.height - 25, 20, 20, Text.of("<-")) {
            close()
        })
        this.addDrawableChild(ButtonWidget(5, 5, 98, 20, Text.of("Client Values")) {
            MinecraftClient.getInstance().setScreen(ScreenBetterParentPopupSettings(this, "Client Values", TarasandeMain.get().clientValues))
        })

        val endHeight = TarasandeMain.get().managerClientMenu.list.filter { element -> (element !is ElementMenuTitle || TarasandeMain.get().clientValues.clientMenuCategories.value) && element.visible() }.size * (buttonHeight + spacer)

        var index = 0
        for (menu in TarasandeMain.get().managerClientMenu.list) {
            if (!menu.visible()) continue
            if (!TarasandeMain.get().clientValues.clientMenuCategories.value && menu is ElementMenuTitle) continue
            this.addDrawableChild(menu.buildWidget(startX, this.halfHeight() - endHeight / 2 + (index * (buttonHeight + spacer)), buttonWidth, buttonHeight))
            index++
        }
    }
}
