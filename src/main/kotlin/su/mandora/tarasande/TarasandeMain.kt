package su.mandora.tarasande

import com.google.gson.GsonBuilder
import net.minecraft.client.MinecraftClient
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventSuccessfulLoad
import su.mandora.tarasande.feature.friend.Friends
import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.feature.screen.Screens
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
import sun.misc.Unsafe
import java.util.logging.Logger


/**
 * TODO:
 *  - User configs
 *  - Crystal Aura / Anchor Aura
 *  - New Chunks
 *  - Container saver (Shop saver...)
 *  - Anti vanish: Tab completion
 *  - AirPlace/LiquidInteract
 *  - Rewrite ESP
 *  - ObstacleSpeed
 *  - Improve projectile aim bot
 *  - Teleport commands taking entities
 *  - Rewrite ProjectileUtil
 *  - Constants for slot list and text field screens
 *  - List of valid chest titles
 *  - List of spammer messages
 *  - Re-Sprint when slowdowned
 *  - Trigger bot
 *  - fix fall flying when ground spoof no fall
 *  - replace string concats with builders
 */

const val TARASANDE_NAME = "tarasande" // "lowercase gang" ~kennytv
val logger = Logger.getLogger(TARASANDE_NAME)!!
val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()!!

val unsafe by lazy { Unsafe::class.java.getDeclaredField("theUnsafe").apply { isAccessible = true }.get(null) as Unsafe }

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
        Screens

        EventDispatcher.call(EventSuccessfulLoad())
    }
}
