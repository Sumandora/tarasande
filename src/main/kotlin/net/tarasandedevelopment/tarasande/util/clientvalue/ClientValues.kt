package net.tarasandedevelopment.tarasande.util.clientvalue

import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.clientmenu.ElementMenuScreen
import net.tarasandedevelopment.tarasande.value.*
import org.lwjgl.glfw.GLFW

class ClientValues {
    
    val menuHotkey = object : ValueBind(this, "Menu: hotkey", Type.KEY, GLFW.GLFW_KEY_RIGHT_SHIFT) {
        override fun filter(bind: Int) = bind != GLFW.GLFW_KEY_UNKNOWN
    }
    val menuAnimationLength = ValueNumber(this, "Menu: animation length", 0.0, 100.0, 500.0, 1.0)
    val menuAccentBackground = ValueBoolean(this, "Menu: accent background", true)
    val menuBlurBackground = ValueBoolean(this, "Menu: blur background", true)
    val menuDrawImage = ValueBoolean(this, "Menu: draw image", true)
    val accentColor = ValueColor(this, "Accent color", 0.6f, 1.0f, 1.0f)
    val targets = ValueMode(this, "Targets", true, "Players", "Animals", "Mobs", "Other")
    val dontAttackTamedEntities = object : ValueBoolean(this, "Don't attack tamed entities", false) {
        override fun isEnabled() = targets.isSelected(1)
    }
    val correctMovement = ValueMode(this, "Correct movement", false, "Off", "Prevent Backwards Sprinting", "Direct", "Silent")
    val blurStrength = object : ValueNumber(this, "Blur strength", 1.0, 1.0, 20.0, 1.0) {
        override fun onChange() {
            TarasandeMain.get().blur.kawasePasses = null
        }
    }
    val unlockTicksPerFrame = ValueBoolean(this, "Unlock ticks per frame", false)
    val updateRotationsWhenTickSkipping = ValueBoolean(this, "Update rotations when tick skipping", false)
    val updateRotationsAccurately = object : ValueBoolean(this, "Update rotations accurately", true) {
        override fun isEnabled() = updateRotationsWhenTickSkipping.value
    }
    val autoSaveConfig = object : ValueBoolean(this, "Auto save: config", true) {
        override fun onChange() {
            TarasandeMain.get().autoSaveDaemon.name = TarasandeMain.get().autoSaveDaemonName + if (!value) " (disabled)" else ""
        }
    }
    val autoSaveDelay = object : ValueNumber(this, "Auto save: delay", 0.0, 10000.0, 60000.0, 1000.0) {
        override fun isEnabled() = autoSaveConfig.value
    }
    val hypixelApiKey = ValueText(this, "Hypixel API Key", "")

    val clientMenuFocusedEntry: ValueMode
    val clientMenuCategories = ValueBoolean(this, "Client Menu: Categories", true)
    val autoFocusTerminal = ValueBoolean(this, "Auto focus terminal", false)

    init {
        val entries = mutableListOf("None")
        entries.addAll(TarasandeMain.get().managerClientMenu.list.filterIsInstance<ElementMenuScreen>().map { e -> e.name })

        clientMenuFocusedEntry = ValueMode(this, "Client Menu: focused entry", false, *entries.toTypedArray())
    }
}
