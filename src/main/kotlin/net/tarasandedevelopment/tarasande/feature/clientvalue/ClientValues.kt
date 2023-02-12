package net.tarasandedevelopment.tarasande.feature.clientvalue

import net.tarasandedevelopment.tarasande.TARASANDE_NAME
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.feature.clientvalue.impl.*
import net.tarasandedevelopment.tarasande.feature.clientvalue.panel.PanelElementsClientValues
import net.tarasandedevelopment.tarasande.feature.rotation.Rotations
import net.tarasandedevelopment.tarasande.system.base.filesystem.ManagerFile
import net.tarasandedevelopment.tarasande.system.base.valuesystem.ManagerValue
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBind
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueColor
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
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
    private val autoSaveConfig = object : ValueBoolean(this, "Auto save: config", true) {
        override fun onChange(oldValue: Boolean?, newValue: Boolean) {
            autoSaveDaemon.name = autoSaveDaemonName + if (!newValue) " (disabled)" else ""
        }
    }
    private val autoSaveDelay = ValueNumber(this, "Auto save: delay", 0.0, 10000.0, 60000.0, 1000.0, isEnabled = { autoSaveConfig.value })

    init {
        ValueButtonOwnerValues(this, "Privacy values", PrivacyValues)
        ValueButtonOwnerValues(this, "Panels values", ManagerPanel.screenPanel)
        ValueButtonOwnerValues(this, "Accessibility values", AccessibilityValues)
        ValueButtonOwnerValues(this, "Network values", NetworkValues)
        ValueButtonOwnerValues(this, "Targeting values", TargetingValues)
        ValueButtonOwnerValues(this, "Debug values", DebugValues)
        ValueButtonOwnerValues(this, "Blur values", ManagerBlur)
        ValueButtonOwnerValues(this, "Rotation values", Rotations)
        ValueButtonOwnerValues(this, "Command values", ManagerCommand)
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
    }

    const val autoSaveDaemonName = "$TARASANDE_NAME config auto save daemon"
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
