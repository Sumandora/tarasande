package net.tarasandedevelopment.tarasande.util.clientvalue

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.Tameable
import net.minecraft.util.registry.Registry
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.blur.BlurKawase
import net.tarasandedevelopment.tarasande.event.EventIsEntityAttackable
import net.tarasandedevelopment.tarasande.util.dummies.DummyWorld
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.value.*
import org.lwjgl.glfw.GLFW

class ClientValues {

    val menuHotkey = object : ValueBind(this, "Menu: hotkey", Type.KEY, GLFW.GLFW_KEY_RIGHT_SHIFT) {
        override fun filter(type: Type, bind: Int) = bind != GLFW.GLFW_KEY_UNKNOWN
    }
    val menuAnimationLength = ValueNumber(this, "Menu: animation length", 0.0, 100.0, 500.0, 1.0)
    val menuAccentBackground = ValueBoolean(this, "Menu: accent background", true)
    val menuBlurBackground = ValueBoolean(this, "Menu: blur background", true)
    val menuDrawImage = ValueBoolean(this, "Menu: draw image", true)
    val menuImage = object : ValueMode(this, "Menu: image", false, "Rimuru", "Shuya's girl", "Nanakusa", "Jannick", "Azusa") {
        override fun isEnabled() = menuDrawImage.value
        override fun onChange() {
            TarasandeMain.get().screenCheatMenu.image = RenderUtil.createImage(selected[0].lowercase().replace(" ", "").replace("'", "") + ".png")
        }
    }
    val accentColor = ValueColor(this, "Accent color", 0.6f, 1.0f, 1.0f)
    val entities = object : ValueRegistry<EntityType<*>>(this, "Entities", Registry.ENTITY_TYPE, EntityType.PLAYER) {

        val map = HashMap<EntityType<*>, Boolean>()

        init {
            val world = DummyWorld()
            Registry.ENTITY_TYPE.forEach {
                map[it] = it.create(world) is LivingEntity
            }
            world.close()
        }

        override fun getTranslationKey(key: Any?) = (key as EntityType<*>).translationKey
        override fun filter(key: EntityType<*>) = map[key] == true
    }
    private val dontAttackTamedEntities = object : ValueBoolean(this, "Don't attack tamed entities", false) {

        val map = HashMap<EntityType<*>, Boolean>()

        init {
            val world = DummyWorld()
            Registry.ENTITY_TYPE.forEach {
                map[it] = it.create(world) is Tameable
            }
            world.close()
        }

        override fun isEnabled() = entities.list.any { map[it] == true }
    }
    private val dontAttackRidingEntity = object : ValueBoolean(this, "Don't attack riding entity", false) {
        override fun isEnabled() = entities.list.isNotEmpty()
    }
    val correctMovement = ValueMode(this, "Correct movement", false, "Off", "Prevent Backwards Sprinting", "Direct", "Silent")
    val blurMode = ValueMode(this, "Blur mode", false, *TarasandeMain.get().managerBlur.list.map { it.name }.toTypedArray())
    val blurStrength = object : ValueNumber(this, "Blur strength", 1.0, 1.0, 20.0, 1.0) {
        override fun onChange() {
            TarasandeMain.get().managerBlur.get(BlurKawase::class.java).kawasePasses = null
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
    val passEventsInScreens = ValueBoolean(this, "Pass events in screens", true)
    val commands = ValueBoolean(this, "Commands", true)
    val commandsPrefix = object : ValueText(this, "Commands: prefix", "$") {
        override fun isEnabled() = commands.value
    }
    val commandsExceptions = object : ValueBoolean(this, "Commands: show exceptions", true) {
        override fun isEnabled() = commands.value
    }
    val bypassCommands = object : ValueBind(this, "Bypass commands", Type.KEY, GLFW.GLFW_KEY_UNKNOWN) {
        override fun isEnabled() = commands.value
        override fun filter(type: Type, bind: Int) = bind >= GLFW.GLFW_KEY_ESCAPE || bind == GLFW.GLFW_KEY_UNKNOWN
    }

    fun isEntityDesired(entity: Entity): Boolean {
        if (dontAttackRidingEntity.value && entity == MinecraftClient.getInstance().player?.vehicle) return false
        if (dontAttackTamedEntities.value && entity is Tameable && entity.ownerUuid == MinecraftClient.getInstance().player?.uuid) return false
        if (!entities.list.contains(entity.type)) return false

        return true
    }

    init {
        TarasandeMain.get().eventDispatcher.add(EventIsEntityAttackable::class.java) { event ->
            event.attackable = event.attackable && isEntityDesired(event.entity)
        }
    }
}
