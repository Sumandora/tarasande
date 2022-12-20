package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.TitleScreen
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList

class ScreenExtensionButtonListTitleScreen : ScreenExtensionButtonList<TitleScreen>(TitleScreen::class.java) {

    init {
        add("Client Values") {
            MinecraftClient.getInstance().setScreen(ScreenBetterOwnerValues(MinecraftClient.getInstance().currentScreen!!, "Client Values", TarasandeMain.clientValues()))
        }
    }
}
