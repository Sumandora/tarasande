package net.tarasandedevelopment.tarasande

import com.google.gson.GsonBuilder
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.feature.clientvalue.ClientValues
import net.tarasandedevelopment.tarasande.feature.friend.Friends
import net.tarasandedevelopment.tarasande.feature.notification.Notifications
import net.tarasandedevelopment.tarasande.system.base.filesystem.ManagerFile
import net.tarasandedevelopment.tarasande.system.base.valuesystem.ManagerValue
import net.tarasandedevelopment.tarasande.system.feature.clickmethodsystem.ManagerClickMethod
import net.tarasandedevelopment.tarasande.system.feature.espsystem.ManagerESP
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule
import net.tarasandedevelopment.tarasande.system.screen.blursystem.ManagerBlur
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.ManagerGraph
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.ManagerInformation
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.ManagerPanel
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension
import su.mandora.event.EventDispatcher
import java.io.File
import java.util.logging.Logger

class TarasandeMain {

    val name = "tarasande" // "lowercase gang" ~kennytv

    val logger = Logger.getLogger(name)!!
    val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()!!
    val rootDirectory = File(System.getProperty("user.home") + File.separator + name)

    private val managerFile = ManagerFile()
    private val managerValue = ManagerValue(managerFile)

    private lateinit var managerESP: ManagerESP
    private lateinit var managerClickMethod: ManagerClickMethod
    private lateinit var managerScreenExtension: ManagerScreenExtension
    private lateinit var managerBlur: ManagerBlur
    private lateinit var managerPanel: ManagerPanel

    private lateinit var managerInformation: ManagerInformation
    private lateinit var managerGraph: ManagerGraph

    private lateinit var managerModule: ManagerModule

    private lateinit var clientValues: ClientValues
    private lateinit var friends: Friends
    private lateinit var notifications: Notifications

    companion object {
        private val instance = TarasandeMain()
        fun get() = instance

        fun managerBlur() = instance.managerBlur
        fun managerValue() = instance.managerValue
        fun managerModule() = instance.managerModule
        fun managerPanel() = instance.managerPanel
        fun managerScreenExtension() = instance.managerScreenExtension
        fun managerInformation() = instance.managerInformation
        fun managerGraph() = instance.managerGraph
        fun managerClickMethod() = instance.managerClickMethod
        fun managerESP() = instance.managerESP
        fun managerFile() = instance.managerFile
        fun clientValues() = instance.clientValues
        fun friends() = instance.friends
        fun notifications() = instance.notifications
    }

    fun onLateLoad() {
        managerPanel = ManagerPanel(managerFile)
        managerInformation = ManagerInformation(managerPanel)
        managerGraph = ManagerGraph(managerInformation, managerPanel)

        managerESP = ManagerESP()
        managerClickMethod = ManagerClickMethod()
        managerScreenExtension = ManagerScreenExtension()
        managerBlur = ManagerBlur()

        managerModule = ManagerModule(managerPanel, managerFile)

        clientValues = ClientValues(name, managerPanel, managerFile)
        friends = Friends()
        notifications = Notifications()

        EventDispatcher.call(EventSuccessfulLoad())
    }
}
