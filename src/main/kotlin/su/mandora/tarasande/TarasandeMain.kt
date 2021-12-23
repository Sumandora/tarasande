package su.mandora.tarasande

import com.google.gson.GsonBuilder
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.Session
import su.mandora.tarasande.base.event.ManagerEvent
import su.mandora.tarasande.base.file.ManagerFile
import su.mandora.tarasande.base.module.ManagerModule
import su.mandora.tarasande.base.util.player.clickspeed.ManagerClickMethod
import su.mandora.tarasande.base.value.ManagerValue
import su.mandora.tarasande.screen.Screens
import su.mandora.tarasande.util.clientvalue.ClientValues
import su.mandora.tarasande.util.entitycolor.EntityColor
import su.mandora.tarasande.util.render.blur.Blur
import su.mandora.tarasande.util.spotify.Spotify
import java.io.PrintWriter
import java.io.StringWriter

/**
 * TODO Modules:	Vehicle Speed
 * 					Elytra Flight
 * 		Baritone Sprint Jump
 * 		KillAura Noise Function Rotations
 * 		Notification System
 * 		Speed - Timer Settings maybe
 * 		Balance Timer
 * 		Friends
 */
class TarasandeMain {

	val name = "tarasande" // "lowercase gang" ~kennytv

	var managerEvent: ManagerEvent? = null
	var managerFile: ManagerFile? = null
	var managerValue: ManagerValue? = null
	var clientValues: ClientValues? = null
	var entityColor: EntityColor? = null
	var managerClickMethod: ManagerClickMethod? = null
	var managerModule: ManagerModule? = null
	var blur: Blur? = null
	var screens: Screens? = null

	val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()!!

	val logString = StringWriter()
	val log = PrintWriter(logString)

	companion object {
		private val instance: TarasandeMain = TarasandeMain()

		fun get(): TarasandeMain {
			return instance
		}
	}

	fun onPreLoad() {
		managerEvent = ManagerEvent()
	}

	fun onLateLoad() {
		managerFile = ManagerFile()
		managerValue = ManagerValue()
		clientValues = ClientValues()
		entityColor = EntityColor()
		managerClickMethod = ManagerClickMethod()
		managerModule = ManagerModule()
		blur = Blur()
		screens = Screens() // Initializes ClickGUI (Make sure that modules and values are initialized before)

		if (System.getProperty("os.name").lowercase().contains("windows")) {
			Spotify.addCallback {
				log.println("Now playing: $it")
			}
		}

		managerFile?.load()

		if(MinecraftClient.getInstance().session?.accountType == Session.AccountType.LEGACY && screens?.betterScreenAccountManager?.mainAccount != -1) {
			screens?.betterScreenAccountManager?.logIn(screens?.betterScreenAccountManager?.accounts!![screens?.betterScreenAccountManager?.mainAccount!!])
			while(screens?.betterScreenAccountManager?.loginThread != null && screens?.betterScreenAccountManager?.loginThread!!.isAlive)
				Thread.sleep(50L) // synchronize
			screens?.betterScreenAccountManager?.status = null
		}
	}

	fun onUnload() {
		managerFile?.save()
	}

}