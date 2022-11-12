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
import net.tarasandedevelopment.tarasande.systems.screen.accountmanager.account.ManagerAccount
import net.tarasandedevelopment.tarasande.systems.screen.accountmanager.azureapp.ManagerAzureApp
import net.tarasandedevelopment.tarasande.systems.screen.accountmanager.environment.ManagerEnvironment
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

    //@formatter:off
    // Features
    val protocolHack    by lazy { TarasandeProtocolHack(rootDirectory) }
    val clientValues    = ClientValues(name, managerPanel)
    val friends         = Friends()
    //@formatter:on

    companion object {
        //@formatter:off

        // Manager (no dependencies)
        val managerValue        = ManagerValue()
            @JvmName("managerValue") get
        internal val managerPackage = ManagerPackage()
            @JvmName("managerPackage") get
        val managerESP          by lazy { ManagerESP() }
            @JvmName("managerESP") get
        val managerClickMethod  = ManagerClickMethod()
            @JvmName("managerClickMethod") get
        val managerScreenExtension by lazy { ManagerScreenExtension() }
            @JvmName("managerScreenExtension") get
        val managerBlur         by lazy { ManagerBlur() }
            @JvmName("managerScreenExtension") get
        val managerClientMenu   by lazy { ManagerClientMenu() }
            @JvmName("managerClientMenu") get
        val managerPanel        by lazy { ManagerPanel() }
            @JvmName("managerPanel") get

        // Manager (one dependency)
        val managerInformation  by lazy { ManagerInformation(managerPanel) }
            @JvmName("managerInformation") get
        val managerModule       by lazy { ManagerModule(managerPanel) }
            @JvmName("managerModule") get

        // Manager (two dependencies)
        val managerGraph        = ManagerGraph(managerInformation, managerPanel)
            @JvmName("managerGraph") get

        // Account Manager
        val managerAccount      = ManagerAccount()
            @JvmName("managerAccount") get
        val managerEnvironment  = ManagerEnvironment()
            @JvmName("managerEnvironment") get
        val managerAzureApp     = ManagerAzureApp()
            @JvmName("managerAzureApp") get

        //@formatter:on

        val instance by lazy { TarasandeMain() }
            @JvmName("get") get
    }

    fun onLateLoad() {
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