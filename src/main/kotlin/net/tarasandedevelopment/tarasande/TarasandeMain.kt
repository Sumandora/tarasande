package net.tarasandedevelopment.tarasande

import com.google.gson.GsonBuilder
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.Session
import net.minecraft.util.Util
import net.tarasandedevelopment.tarasande.base.addon.ManagerAddon
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.ManagerClientMenu
import net.tarasandedevelopment.tarasande.base.esp.ManagerESP
import net.tarasandedevelopment.tarasande.base.event.ManagerEvent
import net.tarasandedevelopment.tarasande.base.file.ManagerFile
import net.tarasandedevelopment.tarasande.base.module.ManagerModule
import net.tarasandedevelopment.tarasande.base.util.player.clickspeed.ManagerClickMethod
import net.tarasandedevelopment.tarasande.base.value.ManagerValue
import net.tarasandedevelopment.tarasande.protocol.TarasandeProtocolHack
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.clientmenu.ElementMenuScreenAccountManager
import net.tarasandedevelopment.tarasande.util.clientvalue.ClientValues
import net.tarasandedevelopment.tarasande.util.connection.Proxy
import net.tarasandedevelopment.tarasande.util.player.friends.Friends
import net.tarasandedevelopment.tarasande.util.player.tagname.TagName
import net.tarasandedevelopment.tarasande.util.render.blur.Blur
import org.slf4j.LoggerFactory
import java.io.File

class TarasandeMain {

    val name = "tarasande" // "lowercase gang" ~kennytv

    lateinit var managerEvent: ManagerEvent
        private set
    private lateinit var managerFile: ManagerFile
    lateinit var managerValue: ManagerValue
        private set
    lateinit var managerAddon: ManagerAddon
        private set
    lateinit var clientValues: ClientValues
        private set
    lateinit var protocolHack: TarasandeProtocolHack
        private set
    lateinit var managerClickMethod: ManagerClickMethod
        private set
    lateinit var managerModule: ManagerModule
        private set
    lateinit var blur: Blur
        private set
    lateinit var screenCheatMenu: ScreenCheatMenu
        private set
    lateinit var friends: Friends
        private set
    lateinit var managerESP: ManagerESP
        private set
    lateinit var managerClientMenu: ManagerClientMenu
        private set
    lateinit var tagName: TagName
        private set

    val logger = LoggerFactory.getLogger(name)!!

    val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()!!

    val autoSaveDaemonName = "$name config auto save daemon"
    val autoSaveDaemon: Thread /* This has to be here because IntelliJ is the best IDE ever built */ = Thread({
        while (true) {
            if (clientValues.autoSaveConfig.value) {
                managerFile.save(false)
                Thread.sleep(clientValues.autoSaveDelay.value.toLong())
            }
        }
    }, autoSaveDaemonName)

    val rootDirectory = File(System.getProperty("user.home") + File.separator + name)

    var proxy: Proxy? = null
    var disabled = false

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
        managerAddon = ManagerAddon()
        managerClientMenu = ManagerClientMenu()
        clientValues = ClientValues()
        managerClickMethod = ManagerClickMethod()
        managerModule = ManagerModule()
        managerESP = ManagerESP()
        blur = Blur()
        protocolHack = TarasandeProtocolHack()
        screenCheatMenu = ScreenCheatMenu() // Initializes ClickGUI (Make sure that modules, values, blur etc... is initialized before)
        friends = Friends()
        tagName = TagName()

        managerFile.load()

        val accountManager = managerClientMenu.get(ElementMenuScreenAccountManager::class.java).screenBetterAccountManager

        if (MinecraftClient.getInstance().session?.accountType == Session.AccountType.LEGACY && accountManager.mainAccount != null) {
            accountManager.logIn(accountManager.accounts[accountManager.mainAccount!!])

            while (accountManager.loginThread != null && accountManager.loginThread!!.isAlive)
                Thread.sleep(50L) // synchronize

            accountManager.status = ""
        }

        autoSaveDaemon.start()
        // We can't guarantee that qdbus exists, nor can we guarantee that we are even using kde plasma, just hope for the best ^^
        if (Util.getOperatingSystem() == Util.OperatingSystem.LINUX) {
            try {
                Runtime.getRuntime().exec("qdbus org.kde.KWin /Compositor suspend")
            } catch (ignored: Throwable) {
            }
        }
    }

    fun onUnload() {
        if (Util.getOperatingSystem() == Util.OperatingSystem.LINUX) {
            try {
                Runtime.getRuntime().exec("qdbus org.kde.KWin /Compositor resume")
            } catch (ignored: Throwable) {
            }
        }

        managerFile.save(true)
    }
}