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

    val autoSaveDaemonName = "$name config auto save daemon"
    val autoSaveDaemon: Thread /* This has to be here because IntelliJ is the best IDE ever built */ = Thread({
        while (true) {
            if (clientValues?.autoSaveConfig?.value!!) {
                managerFile?.save()
                Thread.sleep(clientValues?.delay?.value?.toLong()!!)
            }
        }
    }, autoSaveDaemonName)

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
        managerESP = ManagerESP()
        blur = Blur()
        screens = Screens() // Initializes ClickGUI (Make sure that modules, values, blur etc... is initialized before)
        friends = Friends()

        managerFile?.load()

        if (MinecraftClient.getInstance().session?.accountType == Session.AccountType.LEGACY && screens?.screenBetterAccountManager?.mainAccount != null) {
            screens?.screenBetterAccountManager?.logIn(screens?.screenBetterAccountManager?.accounts!![screens?.screenBetterAccountManager?.mainAccount!!])
            while (screens?.screenBetterAccountManager?.loginThread != null && screens?.screenBetterAccountManager?.loginThread?.isAlive!!) Thread.sleep(50L) // synchronize
            screens?.screenBetterAccountManager?.status = null
        }

        autoSaveDaemon.start()
    }

    fun onUnload() {
        managerFile?.save()
    }
}