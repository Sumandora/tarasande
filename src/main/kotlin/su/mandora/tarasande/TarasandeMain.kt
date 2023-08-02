package su.mandora.tarasande

import com.google.gson.GsonBuilder
import net.minecraft.client.MinecraftClient
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventSuccessfulLoad
import su.mandora.tarasande.feature.friend.Friends
import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.feature.statusrenderer.StatusRenderer
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues
import su.mandora.tarasande.system.base.filesystem.ManagerFile
import su.mandora.tarasande.system.base.valuesystem.ManagerValue
import su.mandora.tarasande.system.feature.commandsystem.ManagerCommand
import su.mandora.tarasande.system.feature.espsystem.ManagerESP
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule
import su.mandora.tarasande.system.screen.blursystem.ManagerBlur
import su.mandora.tarasande.system.screen.graphsystem.ManagerGraph
import su.mandora.tarasande.system.screen.informationsystem.ManagerInformation
import su.mandora.tarasande.system.screen.panelsystem.ManagerPanel
import su.mandora.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension
import java.util.logging.Logger

/**
 * TODO:
 *  - User configs
 *  - Crystal Aura
 *  - New Chunks
 *  - Skin Derp
 *  - Container saver (Shop saver...)
 *  - Anti vanish: Tab completion
 *  - AirPlace/LiquidInteract
 */

const val TARASANDE_NAME = "tarasande" // "lowercase gang" ~kennytv
val logger = Logger.getLogger(TARASANDE_NAME)!!
val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()!!
val mc: MinecraftClient
    get() = MinecraftClient.getInstance()


object TarasandeMain {

    init {
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

        ManagerCommand
        ManagerModule

        TarasandeValues
        Friends
        Rotations
        StatusRenderer

        EventDispatcher.call(EventSuccessfulLoad())
    }
}
