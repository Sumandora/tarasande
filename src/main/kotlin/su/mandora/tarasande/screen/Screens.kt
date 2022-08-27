package su.mandora.tarasande.screen

import net.minecraft.client.MinecraftClient
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.event.EventUpdate
import su.mandora.tarasande.screen.accountmanager.ScreenBetterAccountManager
import su.mandora.tarasande.screen.menu.ScreenMenu

class Screens {

    val betterScreenAccountManager = ScreenBetterAccountManager()
    val screenMenu = ScreenMenu()

    init {
        TarasandeMain.get().managerEvent?.add { event ->
            if (event is EventUpdate)
                if (event.state == EventUpdate.State.PRE)
                    if (TarasandeMain.get().clientValues?.menuHotkey?.wasPressed()!! % 2 == 0)
                        MinecraftClient.getInstance().setScreen(screenMenu)
        }
    }

}