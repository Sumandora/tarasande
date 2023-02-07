package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer

import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.tarasandedevelopment.tarasande.feature.clientvalue.ClientValues
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.accountmanager.ScreenBetterSlotListAccountManager

class ScreenExtensionButtonListMultiplayerScreen : ScreenExtensionButtonList<MultiplayerScreen>(MultiplayerScreen::class.java) {

    init {
        add("Client Values") {
            mc.setScreen(ScreenBetterOwnerValues("Client Values", mc.currentScreen!!, ClientValues))
        }
    }
}