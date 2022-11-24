package net.tarasandedevelopment.tarasande.feature.clientvalue

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.Tameable
import net.minecraft.util.registry.Registry
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventIsEntityAttackable
import net.tarasandedevelopment.tarasande.feature.clientvalue.panel.PanelElementsClientValues
import net.tarasandedevelopment.tarasande.systems.base.filesystem.ManagerFile
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.*
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.meta.ValueButton
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.ManagerPanel
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.screen.impl.ScreenBetterParentValues
import net.tarasandedevelopment.tarasande.util.dummy.ClientWorldDummy
import net.tarasandedevelopment.tarasande.util.extension.Thread
import org.lwjgl.glfw.GLFW
import su.mandora.event.EventDispatcher

class ClientValues(name: String, panelSystem: ManagerPanel, fileSystem: ManagerFile) {

    // General
    val accentColor = ValueColor(this, "Accent color", 0.6f, 1.0f, 1.0f)
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
            MinecraftClient.getInstance().setScreen(ScreenBetterParentValues(MinecraftClient.getInstance().currentScreen!!, this.name, this))
        }
    }

    val disableTelemetry = ValueBoolean(privacy, "Disable telemetry", true)
    val disableRealmsRequests = ValueBoolean(privacy, "Disable realms requests", true)

    init {
        object : ValueButton(this, "Clear binds") {
            override fun onChange() {
                TarasandeMain.managerValue().list.forEach {
                    if(it is ValueBind && it != TarasandeMain.managerPanel().screenCheatMenu.hotkey)
                        it.apply {
                            type = ValueBind.Type.KEY
                            button = GLFW.GLFW_KEY_UNKNOWN
                        }
                }
            }
        }
        object : ValueButton(this, "Cheat menu values") {
            override fun onChange() {
                MinecraftClient.getInstance().setScreen(ScreenBetterParentValues(MinecraftClient.getInstance().currentScreen!!, this.name, panelSystem.screenCheatMenu))
            }
        }
    }

    // Combat
    val targetingValues = object : ValueButton(this, "Targeting values") {
        override fun onChange() {
            MinecraftClient.getInstance().setScreen(ScreenBetterParentValues(MinecraftClient.getInstance().currentScreen!!, this.name, this))
        }
    }
    val entities = object : ValueRegistry<EntityType<*>>(targetingValues, "Entities", Registry.ENTITY_TYPE, EntityType.PLAYER) {

        val map = HashMap<EntityType<*>, Boolean>()

        init {
            val world = ClientWorldDummy()
            Registry.ENTITY_TYPE.forEach {
                map[it] = it.create(world).let { it == null || it is LivingEntity } // Players can't be created and result in null
            }
            world.close()
        }

        override fun getTranslationKey(key: Any?) = (key as EntityType<*>).translationKey
        override fun filter(key: EntityType<*>) = map[key] == true
    }
    private val dontAttackTamedEntities = object : ValueBoolean(targetingValues, "Don't attack tamed entities", false) {

        val map = HashMap<EntityType<*>, Boolean>()

        init {
            val world = ClientWorldDummy()
            Registry.ENTITY_TYPE.forEach {
                map[it] = it.create(world) is Tameable
            }
            world.close()
        }

        override fun isEnabled() = entities.list.any { map[it] == true }
    }
    private val dontAttackRidingEntity = object : ValueBoolean(targetingValues, "Don't attack riding entity", false) {
        override fun isEnabled() = entities.list.isNotEmpty()
    }

    val correctMovement = ValueMode(this, "Correct movement", false, "Off", "Prevent Backwards Sprinting", "Direct", "Silent")

    // Rendering
    init {
        object : ValueButton(this, "Blur values") {
            override fun onChange() {
                MinecraftClient.getInstance().setScreen(ScreenBetterParentValues(MinecraftClient.getInstance().currentScreen!!, "Blur values", TarasandeMain.managerBlur()))
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

    private fun isEntityDesired(entity: Entity): Boolean {
        if (dontAttackRidingEntity.value && entity == MinecraftClient.getInstance().player?.vehicle) return false
        if (dontAttackTamedEntities.value && entity is Tameable && entity.ownerUuid == MinecraftClient.getInstance().player?.uuid) return false
        if (!entities.list.contains(entity.type)) return false

        return true
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
        autoSaveDaemon.start()

        panelSystem.add(PanelElementsClientValues(this))
        EventDispatcher.add(EventIsEntityAttackable::class.java) { event ->
            event.attackable = event.attackable && isEntityDesired(event.entity)
        }
    }
}
