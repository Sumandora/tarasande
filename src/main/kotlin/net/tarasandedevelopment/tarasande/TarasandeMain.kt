package net.tarasandedevelopment.tarasande

import com.google.gson.GsonBuilder
import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.feature.clientvalue.ClientValues
import net.tarasandedevelopment.tarasande.feature.friend.Friends
import net.tarasandedevelopment.tarasande.system.base.filesystem.ManagerFile
import net.tarasandedevelopment.tarasande.system.base.grabbersystem.ManagerGrabber
import net.tarasandedevelopment.tarasande.system.base.valuesystem.ManagerValue
import net.tarasandedevelopment.tarasande.system.feature.espsystem.ManagerESP
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule
import net.tarasandedevelopment.tarasande.system.screen.blursystem.ManagerBlur
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.ManagerGraph
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.ManagerInformation
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.ManagerPanel
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension
import su.mandora.event.EventDispatcher
import java.util.logging.Logger

/**
 * TODO AND BUG TRACKER FOR TARASANDE - (c) Johannes and FlorianMichael
 *
 * TODO | General:
 *  - User configs
 *  - Crystal Aura
 *  - Change Name/Skin/Cape in Account Manager
 *  - Anti vanish: Tab completion
 *  - Render Profiler
 *  - Auto Totem
 *  - AutoLog
 *  - Find max c03 distance constant
 *
 * TODO | Protocol Hack:
 *  - Entity Dimensions in <= 1.19.2
 *  - WindowClick is not fully remapped in <= 1.16.5
 *  - Fix chest rendering and collisions in <= b1.8
 *  - Reimplement sword actions in <= b1.7.3
 *  - Change Boat and Chest Model in <= 1.8
 *  - Bounding boxes of Fences in <= b1.5
 */

const val TARASANDE_NAME = "tarasande" // "lowercase gang" ~kennytv
val logger = Logger.getLogger(TARASANDE_NAME)!!
val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()!!
val mc: MinecraftClient
    get() = MinecraftClient.getInstance()

object TarasandeMain {

    init {
        ManagerGrabber
        ManagerFile
        ManagerValue
    }

    fun onLateLoad() {
        ManagerPanel
        ManagerInformation
        ManagerGraph

        ManagerESP
        ManagerScreenExtension
        ManagerBlur

        ManagerModule

        ClientValues
        Friends

        EventDispatcher.call(EventSuccessfulLoad())
    }
}
