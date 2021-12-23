package su.mandora.tarasande.screen

import net.minecraft.client.MinecraftClient
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.event.EventKey
import su.mandora.tarasande.screen.accountmanager.ScreenBetterAccountManager
import su.mandora.tarasande.screen.menu.ScreenMenu

class Screens {

	val betterScreenAccountManager = ScreenBetterAccountManager()
	val screenMenu = ScreenMenu()

	init {
		TarasandeMain.get().managerEvent?.add { event ->
			if (event is EventKey)
				if (event.key == TarasandeMain.get().clientValues?.menuHotkey?.keyBind)
					MinecraftClient.getInstance().setScreen(screenMenu)
		}
	}

}