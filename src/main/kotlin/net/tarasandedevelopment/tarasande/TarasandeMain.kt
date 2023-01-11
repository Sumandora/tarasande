package net.tarasandedevelopment.tarasande

import com.google.gson.GsonBuilder
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.feature.clientvalue.ClientValues
import net.tarasandedevelopment.tarasande.feature.friend.Friends
import net.tarasandedevelopment.tarasande.feature.notification.Notifications
import net.tarasandedevelopment.tarasande.system.base.filesystem.ManagerFile
import net.tarasandedevelopment.tarasande.system.base.grabbersystem.ManagerGrabber
import net.tarasandedevelopment.tarasande.system.base.valuesystem.ManagerValue
import net.tarasandedevelopment.tarasande.system.feature.clickmethodsystem.ManagerClickMethod
import net.tarasandedevelopment.tarasande.system.feature.commandsystem.ManagerCommand
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

/**
 * TODO AND BUG TRACKER FOR TARASANDE - (c) Johannes and FlorianMichael
 *
 * TODO | General:
 *  - User configs
 *  - Crystal Aura
 *  - Change Name/Skin/Cape in Account Manager
 *  - Anti vanish: Tab completion
 *  - ClickGUI: Correctly save and calculate the positions of the panels when resizing ingame
 *  - Recode File Chooser
 *  - CustomPayload and PacketLogger Panel
 *  - Smartclicking in KillAura
 *  - PathFinder: make values
 *  - Render Profiler
 *  - https://github.com/SirHilarious/WardenTools
 *  - Package DSL
 *  - Regen & Zoot
 *  - Auto Totem
 *  - Blink: Cancelling; Update Velocity and Position
 *
 * TODO | Protocol Hack:
 *  - Entity Dimensions
 *  - WindowClick in 1.16.5 is not fully remapped
 *  - ViaLegacy texture pack using ProtocolPatcher
 *  - Implement BetaCraft ServerList Browser (Maybe own mod)
 *  - Adjust fix World Border in c0.30c
 *  - Reimplement Item Splitter
 */
class TarasandeMain {

    val name = "tarasande" // "lowercase gang" ~kennytv

    val logger = Logger.getLogger(name)!!
    val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()!!
    val rootDirectory = File(System.getProperty("user.home") + File.separator + name)

    private val managerGrabber = ManagerGrabber()
    private val managerFile = ManagerFile()
    private val managerValue = ManagerValue(managerFile)

    private lateinit var managerESP: ManagerESP
    private lateinit var managerClickMethod: ManagerClickMethod
    private lateinit var managerScreenExtension: ManagerScreenExtension
    private lateinit var managerBlur: ManagerBlur
    private lateinit var managerPanel: ManagerPanel

    private lateinit var managerInformation: ManagerInformation
    private lateinit var managerGraph: ManagerGraph

    private lateinit var managerCommand: ManagerCommand
    private lateinit var managerModule: ManagerModule

    private lateinit var clientValues: ClientValues
    private lateinit var friends: Friends
    private lateinit var notifications: Notifications

    companion object {
        private val instance = TarasandeMain()
        fun get() = instance

        fun managerGrabber() = instance.managerGrabber
        fun managerFile() = instance.managerFile
        fun managerValue() = instance.managerValue
        fun managerESP() = instance.managerESP
        fun managerClickMethod() = instance.managerClickMethod
        fun managerScreenExtension() = instance.managerScreenExtension
        fun managerBlur() = instance.managerBlur
        fun managerPanel() = instance.managerPanel
        fun managerInformation() = instance.managerInformation
        fun managerGraph() = instance.managerGraph
        fun managerCommand() = instance.managerCommand
        fun managerModule() = instance.managerModule
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

        managerCommand = ManagerCommand()
        managerModule = ManagerModule(managerCommand, managerPanel, managerFile)

        clientValues = ClientValues(name, managerCommand, managerPanel, managerFile)
        friends = Friends()
        notifications = Notifications()

        EventDispatcher.call(EventSuccessfulLoad())
    }
}
