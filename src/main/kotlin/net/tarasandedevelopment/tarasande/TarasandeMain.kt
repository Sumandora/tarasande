package net.tarasandedevelopment.tarasande

import com.google.gson.GsonBuilder
import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.event.EventDispatcher
import net.tarasandedevelopment.tarasande.event.impl.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.feature.friend.Friends
import net.tarasandedevelopment.tarasande.feature.rotation.Rotations
import net.tarasandedevelopment.tarasande.feature.statusrenderer.StatusRenderer
import net.tarasandedevelopment.tarasande.feature.tarasandevalue.TarasandeValues
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
import java.util.logging.Logger

/**
 * TODO AND BUG TRACKER FOR TARASANDE - (c) Johannes and FlorianMichael
 *
 * TODO | Base:
 *  - User configs
 *  - Crystal Aura
 *  - Change Name/Skin/Cape in Account Manager
 *  - Anti vanish: Tab completion
 *  - Render Profiler
 *  - Dump Items/Blocks/Player
 *  - Re-add Wiki
 *  - Add Rotation RL
 *  - AccountMicrosoft: Change Name on login & Remove Get&Post Methods
 *  - Refactor AccessWidener
 *  - Try out layered maps
 *  - Add Reverse Step (And remove nofall option)
 *
 * TODO | Protocol Spoofer:
 *  - Add basic exploits from bash for Forge
 *  - Crayfish Fill and Nuker
 *
 * TODO | Dupe Exploits:
 *  - https://youtu.be/2lUK-2yo_zo
 *
 * TODO | Litematica:
 *  - Implement PixelArt generator
 *  - Implement head and skin generator
 *
 * TODO | Creative Features:
 *  - rewrite most parts of the system
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

        ManagerCommand
        ManagerModule

        TarasandeValues
        Friends
        Rotations
        StatusRenderer

        EventDispatcher.call(EventSuccessfulLoad())
    }
}
