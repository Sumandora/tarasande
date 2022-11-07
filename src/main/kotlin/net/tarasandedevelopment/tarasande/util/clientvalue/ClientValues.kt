package net.tarasandedevelopment.tarasande.util.clientvalue

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.Tameable
import net.minecraft.util.registry.Registry
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventIsEntityAttackable
import net.tarasandedevelopment.tarasande.render.blur.BlurKawase
import net.tarasandedevelopment.tarasande.util.dummies.ClientWorldDummy
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.value.*
import org.lwjgl.glfw.GLFW

class ClientValues {

    // General
    val accentColor = ValueColor(this, "Accent color", 0.6f, 1.0f, 1.0f)
    val autoSaveConfig = object : ValueBoolean(this, "Auto save: config", true) {
        override fun onChange() {
            TarasandeMain.get().autoSaveDaemon.name = TarasandeMain.get().autoSaveDaemonName + if (!value) " (disabled)" else ""
        }
    }
    val autoSaveDelay = object : ValueNumber(this, "Auto save: delay", 0.0, 10000.0, 60000.0, 1000.0) {
        override fun isEnabled() = autoSaveConfig.value
    }
    val passEventsInScreens = ValueBoolean(this, "Pass events in screens", true)

    // Cheat menu
    val cheatMenuHotkey = object : ValueBind(this, "Cheat menu: hotkey", Type.KEY, GLFW.GLFW_KEY_RIGHT_SHIFT) {
        override fun filter(type: Type, bind: Int) = bind != GLFW.GLFW_KEY_UNKNOWN
    }
    val cheatMenuAnimationLength = ValueNumber(this, "Cheat menu: animation length", 0.0, 100.0, 500.0, 1.0)
    val cheatMenuAccentBackground = ValueBoolean(this, "Cheat menu: accent background", true)
    val cheatMenuBlurBackground = ValueBoolean(this, "Cheat menu: blur background", true)
    val cheatMenuDrawImage = ValueBoolean(this, "Cheat menu: draw image", true)
    val cheatMenuImage = object : ValueMode(this, "Cheat menu: image", false, "Rimuru", "Shuya's girl", "Nanakusa", "Jannick", "Azusa") {
        override fun isEnabled() = cheatMenuDrawImage.value
        override fun onChange() {
            TarasandeMain.get().screenCheatMenu.image = RenderUtil.createImage(selected[0].lowercase().replace(" ", "").replace("'", "") + ".png")
        }
    }

    // Client menu
    val clientMenuShowCategories = ValueBoolean(this, "Client menu: show categories", true)

    // Combat
    val entities = object : ValueRegistry<EntityType<*>>(this, "Entities", Registry.ENTITY_TYPE, EntityType.PLAYER) {

        val map = HashMap<EntityType<*>, Boolean>()

        init {
            val world = ClientWorldDummy()
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
            val world = ClientWorldDummy()
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

    // Rendering
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
    val allowEveryCharacterInChat = ValueBoolean(this, "Allow every character in chat", true)
    val fontRenderer = object : ValueMode(this, "Font renderer", false, *TarasandeMain.get().managerFont.list.map { it.name }.toTypedArray()) {
        override fun onChange() {
            TarasandeMain.get().managerFont.also {
                it.selected = it.list[settings.indexOf(selected[0])]
            }
        }
    }

    private fun isEntityDesired(entity: Entity): Boolean {
        if (dontAttackRidingEntity.value && entity == MinecraftClient.getInstance().player?.vehicle) return false
        if (dontAttackTamedEntities.value && entity is Tameable && entity.ownerUuid == MinecraftClient.getInstance().player?.uuid) return false
        if (!entities.list.contains(entity.type)) return false

        return true
    }

    init {
        TarasandeMain.get().managerEvent.add(EventIsEntityAttackable::class.java) { event ->
            event.attackable = event.attackable && isEntityDesired(event.entity)
        }
    }
}
