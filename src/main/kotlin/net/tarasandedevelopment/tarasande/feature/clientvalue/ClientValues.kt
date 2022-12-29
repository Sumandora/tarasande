package net.tarasandedevelopment.tarasande.feature.clientvalue

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.EntityType
import net.minecraft.entity.Tameable
import net.minecraft.registry.Registries
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventIsEntityAttackable
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.feature.clientvalue.panel.PanelElementsClientValues
import net.tarasandedevelopment.tarasande.system.base.filesystem.ManagerFile
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.*
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.ValueButton
import net.tarasandedevelopment.tarasande.system.feature.commandsystem.ManagerCommand
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.ManagerPanel
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import net.tarasandedevelopment.tarasande.util.extension.javaruntime.Thread
import org.lwjgl.glfw.GLFW
import su.mandora.event.EventDispatcher

class ClientValues(name: String, commandSystem: ManagerCommand, panelSystem: ManagerPanel, fileSystem: ManagerFile) {

    // General
    val accentColor = ValueColor(this, "Accent color", 0.6, 1.0, 1.0)
    val autoSaveConfig = object : ValueBoolean(this, "Auto save: config", true) {
        override fun onChange() {
            autoSaveDaemon.name = autoSaveDaemonName + if (!value) " (disabled)" else ""
        }
    }
    private val autoSaveDelay = object : ValueNumber(this, "Auto save: delay", 0.0, 10000.0, 60000.0, 1000.0) {
        override fun isEnabled() = autoSaveConfig.value
    }

    private val privacy = object : ValueButton(this, "Privacy") {
        override fun onChange() {
            MinecraftClient.getInstance().setScreen(ScreenBetterOwnerValues(MinecraftClient.getInstance().currentScreen!!, this.name, this))
        }
    }

    val disableTelemetry = ValueBoolean(privacy, "Disable telemetry", true)
    val disableRealmsRequests = ValueBoolean(privacy, "Disable realms requests", true)

    init {
        object : ValueButton(this, "Cheat menu values") {
            override fun onChange() {
                MinecraftClient.getInstance().setScreen(ScreenBetterOwnerValues(MinecraftClient.getInstance().currentScreen!!, this.name, panelSystem.screenCheatMenu))
            }
        }
        object : ValueButton(this, "Command values") {
            override fun onChange() {
                MinecraftClient.getInstance().setScreen(ScreenBetterOwnerValues(MinecraftClient.getInstance().currentScreen!!, this.name, commandSystem))
            }
        }
    }

    val allowAllCharactersInChat = ValueBoolean(this, "Allow all characters in chat", true)
    val allowAddressParsingForBlacklistedServers = ValueBoolean(this, "Allow address parsing for blacklisted servers", true)

    // Combat
    val targetingValues = object : ValueButton(this, "Targeting values") {
        override fun onChange() {
            MinecraftClient.getInstance().setScreen(ScreenBetterOwnerValues(MinecraftClient.getInstance().currentScreen!!, this.name, this))
        }
    }
    val entities = object : ValueRegistry<EntityType<*>>(targetingValues, "Entities", Registries.ENTITY_TYPE, EntityType.PLAYER) {
        init {
            EventDispatcher.add(EventIsEntityAttackable::class.java) {
                it.attackable = it.attackable && list.contains(it.entity.type)
            }
        }

        override fun getTranslationKey(key: Any?) = (key as EntityType<*>).translationKey
    }

    init {
        object : ValueBoolean(targetingValues, "Don't attack tamed entities", false) {
            init {
                EventDispatcher.add(EventIsEntityAttackable::class.java) {
                    if (value)
                        it.attackable = it.attackable && (it.entity !is Tameable || it.entity.ownerUuid != MinecraftClient.getInstance().player?.uuid)
                }
            }
        }
        object : ValueBoolean(targetingValues, "Don't attack riding entity", false) {
            init {
                EventDispatcher.add(EventIsEntityAttackable::class.java) {
                    if (value)
                        it.attackable = it.attackable && it.entity != MinecraftClient.getInstance().player?.vehicle
                }
            }

            override fun isEnabled() = entities.list.isNotEmpty()
        }
    }

    val correctMovement = ValueMode(this, "Correct movement", false, "Off", "Prevent Backwards Sprinting", "Direct", "Silent")

    // Rendering
    init {
        object : ValueButton(this, "Blur values") {
            override fun onChange() {
                MinecraftClient.getInstance().setScreen(ScreenBetterOwnerValues(MinecraftClient.getInstance().currentScreen!!, "Blur values", TarasandeMain.managerBlur()))
            }
        }
    }

    // Rotations
    val passEventsInScreens = ValueBoolean(this, "Pass events in screens", true)
    val unlockTicksPerFrame = ValueBoolean(this, "Unlock ticks per frame", false)
    val updateRotationsWhenTickSkipping = ValueBoolean(this, "Update rotations when tick skipping", false)
    val updateRotationsAccurately = object : ValueBoolean(this, "Update rotations accurately", true) {
        override fun isEnabled() = updateRotationsWhenTickSkipping.value
    }

    // Other
    init {
        object : ValueButton(this, "Clear binds") {
            override fun onChange() {
                TarasandeMain.managerValue().list.forEach {
                    if (it is ValueBind && it.filter(ValueBind.Type.KEY, GLFW.GLFW_KEY_UNKNOWN))
                        it.apply {
                            type = ValueBind.Type.KEY
                            button = GLFW.GLFW_KEY_UNKNOWN
                        }
                }
            }
        }
    }

    val autoSaveDaemonName = "$name config auto save daemon"
    val autoSaveDaemon: Thread = Thread(autoSaveDaemonName) {
        while (true) {
            Thread.sleep(autoSaveDelay.value.toLong())
            if (autoSaveConfig.value) {
                fileSystem.save(false)
            }
        }
    }

    init {
        panelSystem.add(PanelElementsClientValues(this))
        EventDispatcher.add(EventSuccessfulLoad::class.java, 10000) {
            autoSaveDaemon.start()
        }
    }
}
