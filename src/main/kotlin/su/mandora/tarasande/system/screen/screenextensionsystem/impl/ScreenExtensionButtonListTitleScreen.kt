package su.mandora.tarasande.system.screen.screenextensionsystem.impl

import net.minecraft.client.gui.screen.TitleScreen
import su.mandora.tarasande.TARASANDE_NAME
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import su.mandora.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList

class ScreenExtensionButtonListTitleScreen : ScreenExtensionButtonList<TitleScreen>(TitleScreen::class.java) {

    init {
        add("$TARASANDE_NAME values") {
            mc.setScreen(ScreenBetterOwnerValues("$TARASANDE_NAME values", mc.currentScreen!!, TarasandeValues))
        }
    }
}