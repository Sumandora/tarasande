package net.tarasandedevelopment.tarasande.feature.clientvalue

import net.tarasandedevelopment.tarasande.TARASANDE_NAME
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.feature.clientvalue.impl.DebugValues
import net.tarasandedevelopment.tarasande.feature.clientvalue.impl.PrivacyValues
import net.tarasandedevelopment.tarasande.feature.clientvalue.impl.RotationValues
import net.tarasandedevelopment.tarasande.feature.clientvalue.impl.TargetingValues
import net.tarasandedevelopment.tarasande.feature.clientvalue.panel.PanelElementsClientValues
import net.tarasandedevelopment.tarasande.system.base.filesystem.ManagerFile
import net.tarasandedevelopment.tarasande.system.base.valuesystem.ManagerValue
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.*
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.ValueButton
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import net.tarasandedevelopment.tarasande.system.feature.commandsystem.ManagerCommand
import net.tarasandedevelopment.tarasande.system.screen.blursystem.ManagerBlur
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.ManagerPanel
import net.tarasandedevelopment.tarasande.util.extension.javaruntime.Thread
import org.lwjgl.glfw.GLFW
import su.mandora.event.EventDispatcher

object ClientValues {

    // General
    val accentColor = ValueColor(this, "Accent color", 0.6, 1.0, 1.0)
    val autoSaveConfig = object : ValueBoolean(this, "Auto save: config", true) {
        override fun onChange(oldValue: Boolean?, newValue: Boolean) {
            if (autoSaveDaemon != null)
                autoSaveDaemon.name = autoSaveDaemonName + if (!newValue) " (disabled)" else ""
        }
    }
    private val autoSaveDelay = object : ValueNumber(this, "Auto save: delay", 0.0, 10000.0, 60000.0, 1000.0) {
        override fun isEnabled() = autoSaveConfig.value
    }

    init {
        ValueButtonOwnerValues(this, "Privacy values", PrivacyValues)
        ValueButtonOwnerValues(this, "Cheat menu values", ManagerPanel.screenCheatMenu)
        ValueButtonOwnerValues(this, "Command values", ManagerCommand)
    }

    val allowAddressParsingForBlacklistedServers = ValueBoolean(this, "Allow address parsing for blacklisted servers", true)
    val removeNettyExceptionHandling = ValueMode(this, "Remove Netty exception handling", true, "Timeout", "Wrong packets")

    init {
        // Combat
        ValueButtonOwnerValues(this, "Targeting values", TargetingValues)
        // Rendering
        ValueButtonOwnerValues(this, "Blur values", ManagerBlur)
        // Rotations
        ValueButtonOwnerValues(this, "Rotation values", RotationValues)
    }

    val passEventsInScreens = ValueBoolean(this, "Pass events in screens", true)
    val unlockTicksPerFrame = ValueBoolean(this, "Unlock ticks per frame", false)

    // Other
    init {
        object : ValueButton(this, "Clear binds") {
            override fun onClick() {
                ManagerValue.list.forEach {
                    if (it is ValueBind && it.filter(ValueBind.Type.KEY, GLFW.GLFW_KEY_UNKNOWN))
                        it.apply {
                            type = ValueBind.Type.KEY
                            button = GLFW.GLFW_KEY_UNKNOWN
                        }
                }
            }
        }
        ValueButtonOwnerValues(this, "Debug values", DebugValues)
    }

    val autoSaveDaemonName = "$TARASANDE_NAME config auto save daemon"
    val autoSaveDaemon: Thread = Thread(autoSaveDaemonName) {
        while (true) {
            Thread.sleep(autoSaveDelay.value.toLong())
            if (autoSaveConfig.value) {
                ManagerFile.save(false)
            }
        }
    }

    init {
        EventDispatcher.add(EventSuccessfulLoad::class.java, 1001) {
            ManagerPanel.add(PanelElementsClientValues(this))
        }
        EventDispatcher.add(EventSuccessfulLoad::class.java, 10000) {
            autoSaveDaemon.start()
        }
    }
}
