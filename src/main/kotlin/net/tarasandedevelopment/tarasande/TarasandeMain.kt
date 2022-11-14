package net.tarasandedevelopment.tarasande

import com.google.gson.GsonBuilder
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.feature.clientvalue.ClientValues
import net.tarasandedevelopment.tarasande.feature.friend.Friends
import net.tarasandedevelopment.tarasande.protocolhack.TarasandeProtocolHack
import net.tarasandedevelopment.tarasande.systems.base.filesystem.ManagerFile
import net.tarasandedevelopment.tarasande.systems.base.packagesystem.ManagerPackage
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.ManagerValue
import net.tarasandedevelopment.tarasande.systems.feature.clickmethodsystem.ManagerClickMethod
import net.tarasandedevelopment.tarasande.systems.feature.espsystem.ManagerESP
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ManagerModule
import net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.ManagerMultiplayerFeature
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.ManagerScreenExtension
import net.tarasandedevelopment.tarasande.systems.screen.blursystem.ManagerBlur
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.ManagerGraph
import net.tarasandedevelopment.tarasande.systems.screen.informationsystem.ManagerInformation
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.ManagerPanel
import net.tarasandedevelopment.tarasande.util.connection.Proxy
import org.slf4j.LoggerFactory
import su.mandora.event.EventDispatcher
import java.io.File

class TarasandeMain {

    val name = "tarasande" // "lowercase gang" ~kennytv

    val logger = LoggerFactory.getLogger(name)!!
    val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()!!
    val rootDirectory = File(System.getProperty("user.home") + File.separator + name)
    var proxy: Proxy? = null
    lateinit var protocolHack: TarasandeProtocolHack

    //@formatter:off
    private val managerFile = ManagerFile()
    private val managerValue = ManagerValue(managerFile)
    private val managerPackage = ManagerPackage()

    private lateinit var managerESP: ManagerESP
    private lateinit var managerClickMethod: ManagerClickMethod
    private lateinit var managerScreenExtension: ManagerScreenExtension
    private lateinit var managerBlur: ManagerBlur
    private lateinit var managerMultiplayerFeature: ManagerMultiplayerFeature
    private lateinit var managerPanel: ManagerPanel

    private lateinit var managerInformation: ManagerInformation
    private lateinit var managerGraph: ManagerGraph

    private lateinit var managerModule: ManagerModule

    private lateinit var clientValues: ClientValues
    private lateinit var friends: Friends
    //@formatter:on

    companion object {
        val instance = TarasandeMain()
        fun get() = instance

        fun managerBlur() = instance.managerBlur
        fun managerValue() = instance.managerValue
        fun managerModule() = instance.managerModule
        fun managerPanel() = instance.managerPanel
        fun managerScreenExtension() = instance.managerScreenExtension
        fun managerMultiplayerFeature() = instance.managerMultiplayerFeature
        fun managerInformation() = instance.managerInformation
        fun protocolHack() = instance.protocolHack
        fun managerClickMethod() = instance.managerClickMethod
        fun managerESP() = instance.managerESP
        internal fun managerPackage() = instance.managerPackage
        fun managerFile() = instance.managerFile
        fun clientValues() = instance.clientValues
        fun friends() = instance.friends
    }

    fun onLateLoad() {
        managerPanel = ManagerPanel(managerFile)
        managerInformation = ManagerInformation(managerPanel)
        managerGraph = ManagerGraph(managerInformation, managerPanel)
        managerMultiplayerFeature = ManagerMultiplayerFeature()

        protocolHack = TarasandeProtocolHack(rootDirectory)

        managerESP = ManagerESP()
        managerClickMethod = ManagerClickMethod()
        managerScreenExtension = ManagerScreenExtension()
        managerBlur = ManagerBlur()

        managerModule = ManagerModule(managerPanel, managerFile)

        clientValues = ClientValues(name, managerPanel, managerFile)
        friends = Friends()

        EventDispatcher.call(EventSuccessfulLoad())
    }
}
