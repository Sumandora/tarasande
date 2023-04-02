package su.mandora.tarasande.feature.tarasandevalue

import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.TARASANDE_NAME
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventSuccessfulLoad
import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.feature.tarasandevalue.impl.*
import su.mandora.tarasande.feature.tarasandevalue.panel.PanelElementsTarasandeValues
import su.mandora.tarasande.system.base.filesystem.ManagerFile
import su.mandora.tarasande.system.base.valuesystem.ManagerValue
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBind
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueColor
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.base.valuesystem.impl.meta.ValueButton
import su.mandora.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import su.mandora.tarasande.system.feature.commandsystem.ManagerCommand
import su.mandora.tarasande.system.screen.blursystem.ManagerBlur
import su.mandora.tarasande.system.screen.panelsystem.ManagerPanel
import su.mandora.tarasande.util.extension.javaruntime.Thread

object TarasandeValues {

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
            ManagerPanel.add(PanelElementsTarasandeValues(this))
        }
        EventDispatcher.add(EventSuccessfulLoad::class.java, 10000) {
            autoSaveDaemon.start()
        }
    }
}
