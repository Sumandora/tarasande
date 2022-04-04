package su.mandora.tarasande

import com.google.gson.GsonBuilder
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.Session
import su.mandora.tarasande.base.esp.ManagerESP
import su.mandora.tarasande.base.event.ManagerEvent
import su.mandora.tarasande.base.file.ManagerFile
import su.mandora.tarasande.base.module.ManagerModule
import su.mandora.tarasande.base.util.player.clickspeed.ManagerClickMethod
import su.mandora.tarasande.base.value.ManagerValue
import su.mandora.tarasande.screen.Screens
import su.mandora.tarasande.util.clientvalue.ClientValues
import su.mandora.tarasande.util.player.entitycolor.EntityColor
import su.mandora.tarasande.util.player.friends.Friends
import su.mandora.tarasande.util.render.blur.Blur

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
    var friends: Friends? = null
    var managerESP: ManagerESP? = null

    val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()!!

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
        friends = Friends()
        managerESP = ManagerESP()

        managerFile?.load()

        if (MinecraftClient.getInstance().session?.accountType == Session.AccountType.LEGACY && screens?.betterScreenAccountManager?.mainAccount != null) {
            screens?.betterScreenAccountManager?.logIn(screens?.betterScreenAccountManager?.accounts!![screens?.betterScreenAccountManager?.mainAccount!!])
            while (screens?.betterScreenAccountManager?.loginThread != null && screens?.betterScreenAccountManager?.loginThread?.isAlive!!)
                Thread.sleep(50L) // synchronize
            screens?.betterScreenAccountManager?.status = null
        }
    }

    fun onUnload() {
        managerFile?.save()
    }
}