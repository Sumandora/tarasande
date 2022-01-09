package su.mandora.tarasande.screen

import net.minecraft.client.MinecraftClient
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.event.EventKey
import su.mandora.tarasande.event.EventMouse
import su.mandora.tarasande.screen.accountmanager.ScreenBetterAccountManager
import su.mandora.tarasande.screen.menu.ScreenMenu
import su.mandora.tarasande.screen.wheel.WheelMenu

class Screens {

	val betterScreenAccountManager = ScreenBetterAccountManager()
	val screenMenu = ScreenMenu()
	private val wheelMenu = WheelMenu()

	init {
		TarasandeMain.get().managerEvent?.add { event ->
			if (event is EventKey) {
				if (event.key == TarasandeMain.get().clientValues?.menuHotkey?.keyBind)
					MinecraftClient.getInstance().setScreen(screenMenu)
			} else if(event is EventMouse) {
				if(MinecraftClient.getInstance().currentScreen == null)
					if(!wheelMenu.active && event.button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
						wheelMenu.active = true
					}
			}
		}
	}

}