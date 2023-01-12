package net.tarasandedevelopment.tarasande

import com.google.gson.GsonBuilder
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.feature.clientvalue.ClientValues
import net.tarasandedevelopment.tarasande.feature.friend.Friends
import net.tarasandedevelopment.tarasande.feature.notification.Notifications
import net.tarasandedevelopment.tarasande.system.base.filesystem.ManagerFile
import net.tarasandedevelopment.tarasande.system.base.grabbersystem.ManagerGrabber
import net.tarasandedevelopment.tarasande.system.base.valuesystem.ManagerValue
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
 *  - Auto Totem
 *
 * TODO | Protocol Hack:
 *  - Entity Dimensions
 *  - WindowClick in 1.16.5 is not fully remapped
 *  - ViaLegacy texture pack using ProtocolPatcher
 *  - Adjust fix World Border in c0.30c
 *  - Reimplement Item Splitter
 */
object TarasandeMain {

    val name = "tarasande" // "lowercase gang" ~kennytv

    val logger = Logger.getLogger(name)!!
    val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()!!
    val rootDirectory = File(System.getProperty("user.home") + File.separator + name)

    init {
        ManagerGrabber
        ManagerFile
        ManagerValue
    }

    lateinit var clientValues: ClientValues
    lateinit var friends: Friends
    lateinit var notifications: Notifications

    fun onLateLoad() {
        ManagerPanel
        ManagerInformation
        ManagerGraph

        ManagerESP
        ManagerScreenExtension
        ManagerBlur

        ManagerCommand
        ManagerModule

        clientValues = ClientValues(name)
        friends = Friends()
        notifications = Notifications()

        EventDispatcher.call(EventSuccessfulLoad())
    }
}
