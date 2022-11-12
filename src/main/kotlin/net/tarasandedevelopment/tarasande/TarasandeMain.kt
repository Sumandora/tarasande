package net.tarasandedevelopment.tarasande

import com.google.gson.GsonBuilder
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.Session
import net.minecraft.util.Util
import net.tarasandedevelopment.event.EventDispatcher
import net.tarasandedevelopment.tarasande.events.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.feature.clientvalue.ClientValues
import net.tarasandedevelopment.tarasande.feature.friends.Friends
import net.tarasandedevelopment.tarasande.protocolhack.TarasandeProtocolHack
import net.tarasandedevelopment.tarasande.protocolhack.platform.ProtocolHackValues
import net.tarasandedevelopment.tarasande.systems.base.packagesystem.ManagerPackage
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.ManagerValue
import net.tarasandedevelopment.tarasande.systems.feature.clickmethodsystem.ManagerClickMethod
import net.tarasandedevelopment.tarasande.systems.feature.espsystem.ManagerESP
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ManagerModule
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.ManagerScreenExtension
import net.tarasandedevelopment.tarasande.systems.screen.blursystem.ManagerBlur
import net.tarasandedevelopment.tarasande.systems.screen.clientmenu.ManagerClientMenu
import net.tarasandedevelopment.tarasande.systems.screen.clientmenu.clientmenu.ElementMenuScreenAccountManager
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.ManagerGraph
import net.tarasandedevelopment.tarasande.systems.screen.informationsystem.ManagerInformation
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.ManagerPanel
import net.tarasandedevelopment.tarasande.util.connection.Proxy
import org.slf4j.LoggerFactory
import java.io.File

class TarasandeMain {

    val name = "tarasande" // "lowercase gang" ~kennytv

    val logger = LoggerFactory.getLogger(name)!!
    val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()!!
    val rootDirectory = File(System.getProperty("user.home") + File.separator + name)
    var proxy: Proxy? = null
    lateinit var protocolHack: TarasandeProtocolHack

    //@formatter:off
    val managerValue = ManagerValue()
    internal val managerPackage = ManagerPackage()
    lateinit var managerESP: ManagerESP
    lateinit var managerClickMethod: ManagerClickMethod
    lateinit var managerScreenExtension: ManagerScreenExtension
    lateinit var managerBlur: ManagerBlur
    lateinit var managerClientMenu: ManagerClientMenu
    lateinit var managerPanel: ManagerPanel

    lateinit var managerInformation: ManagerInformation
    lateinit var managerModule: ManagerModule

    lateinit var managerGraph: ManagerGraph

    // Features
    lateinit var clientValues: ClientValues
    lateinit var friends: Friends
    //@formatter:on

    companion object {
        val instance = TarasandeMain()
        fun get() = instance

        fun managerBlur() = instance.managerBlur
        fun managerValue() = instance.managerValue
        fun managerModule() = instance.managerModule
        fun managerPanel() = instance.managerPanel
        fun managerClientMenu() = instance.managerClientMenu
        fun managerInformation() = instance.managerInformation
        fun managerClickMethod() = instance.managerClickMethod
        fun managerESP() = instance.managerESP
        internal fun managerPackage() = instance.managerPackage
    }

    fun onLateLoad() {
        managerPanel = ManagerPanel()
        managerInformation = ManagerInformation(managerPanel)

        protocolHack = TarasandeProtocolHack(rootDirectory)

        managerESP = ManagerESP()
        managerClickMethod = ManagerClickMethod()
        managerScreenExtension = ManagerScreenExtension()
        managerBlur = ManagerBlur()
        managerClientMenu = ManagerClientMenu()

        managerModule = ManagerModule(managerPanel)

        managerGraph = ManagerGraph(managerInformation, managerPanel)

        clientValues = ClientValues(name, managerPanel)
        friends = Friends()

        EventDispatcher.call(EventSuccessfulLoad())
        ProtocolHackValues.update(ProtocolVersion.getProtocol(protocolHack.version.value.toInt()))

        val accountManager = managerClientMenu.get(ElementMenuScreenAccountManager::class.java).screenBetterSlotListAccountManager
        if (MinecraftClient.getInstance().session?.accountType == Session.AccountType.LEGACY && accountManager.mainAccount != null) {
            accountManager.logIn(accountManager.accounts[accountManager.mainAccount!!])

            while (accountManager.loginThread != null && accountManager.loginThread!!.isAlive)
                Thread.sleep(50L) // synchronize

            accountManager.status = ""
        }

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
            //TODO
            //FileIO.saveAll()
        }
    }
}