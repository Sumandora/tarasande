package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl

import net.minecraft.client.gui.screen.TitleScreen
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.feature.clientvalue.ClientValues
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList
import net.tarasandedevelopment.tarasande.util.extension.mc

class ScreenExtensionButtonListTitleScreen : ScreenExtensionButtonList<TitleScreen>(TitleScreen::class.java) {

    init {
        add("Client Values") {
            mc.setScreen(ScreenBetterOwnerValues("Client Values", mc.currentScreen!!, ClientValues))
        }
    }
}
