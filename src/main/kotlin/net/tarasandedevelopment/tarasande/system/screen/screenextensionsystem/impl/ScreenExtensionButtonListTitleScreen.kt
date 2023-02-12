package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl

import net.minecraft.client.gui.screen.TitleScreen
import net.tarasandedevelopment.tarasande.TARASANDE_NAME
import net.tarasandedevelopment.tarasande.feature.tarasandevalue.TarasandeValues
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList

class ScreenExtensionButtonListTitleScreen : ScreenExtensionButtonList<TitleScreen>(TitleScreen::class.java) {

    init {
        add("$TARASANDE_NAME values") {
            mc.setScreen(ScreenBetterOwnerValues("$TARASANDE_NAME values", mc.currentScreen!!, TarasandeValues))
        }
    }
}