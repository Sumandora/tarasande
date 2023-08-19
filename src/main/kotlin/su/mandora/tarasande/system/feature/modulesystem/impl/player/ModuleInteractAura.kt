package su.mandora.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
import net.minecraft.registry.Registries
import net.minecraft.util.hit.EntityHitResult
import su.mandora.tarasande.event.impl.EventAttack
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.event.impl.EventRotation
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.base.valuesystem.impl.ValueRegistry
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.DEFAULT_REACH
import su.mandora.tarasande.util.extension.javaruntime.clearAndGC
import su.mandora.tarasande.util.extension.kotlinruntime.WeakSet
import su.mandora.tarasande.util.extension.minecraft.isEntityHitResult
import su.mandora.tarasande.util.extension.minecraft.packet.isNewWorld
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande.util.math.rotation.RotationUtil
import su.mandora.tarasande.util.maxReach
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.player.chat.CustomChat
import kotlin.math.min

class ModuleInteractAura : Module("Interact aura", "Interact with entities around you.", ModuleCategory.PLAYER) {

    private val delay = ValueNumber(this, "Delay", 0.0, 200.0, 1000.0, 50.0)
    private val reach = ValueNumber(this, "Reach", 0.1, DEFAULT_REACH, maxReach, 0.1)
    private val entities = object : ValueRegistry<EntityType<*>>(this, "Entities", Registries.ENTITY_TYPE, true, EntityType.COW) {
        override fun getTranslationKey(key: Any?) = (key as EntityType<*>).translationKey
    }
    private val closedInventory = ValueBoolean(this, "Closed inventory", false)
    private val autoCloseScreens = ValueBoolean(this, "Auto close screens", false)
    private val throughWalls = ValueBoolean(this, "Through walls", true)
    private val interactOnce = object : ValueBoolean(this, "Interact once", true) {
        override fun onChange(oldValue: Boolean?, newValue: Boolean) {
            interactedEntities = WeakSet()
        }
    }
    private val maxInteractions = ValueNumber(this, "Max interactions", 1.0, 1.0, 20.0, 1.0)
    private val shuffledOrder = ValueBoolean(this, "Shuffled order", true)
    private var interactedEntities = WeakSet<Entity>()
    private val timeUtil = TimeUtil()

    private var focusedEntities = ArrayList<Pair<Entity, EntityHitResult>>()

    override fun onDisable() {
        interactedEntities = WeakSet()
        focusedEntities.clearAndGC()
    }

    init {
        registerEvent(EventRotation::class.java) { event ->
            focusedEntities.clearAndGC()

            if (!timeUtil.hasReached(delay.value.toLong()))
                return@registerEvent

            val list = mc.world!!.entities
                .filter { entities.isSelected(it.type) }
                .filter { it.boundingBox.center.squaredDistanceTo(mc.player!!.eyePos) < reach.value * reach.value }
                .mapNotNull {
                    val hitResult = PlayerUtil.getTargetedEntity(reach.value, RotationUtil.getRotations(mc.player!!.eyePos, it.boundingBox.center), throughWalls.value)
                    if (!hitResult.isEntityHitResult())
                        return@mapNotNull null
                    return@mapNotNull it to hitResult!!
                }

            for (entity in list
                .distinct()
                .filter { !interactedEntities.contains(it.first) }
                .let { if (shuffledOrder.value) it.shuffled() else it }
                .let { it.subList(0, min(maxInteractions.value.toInt(), it.size)) }
                .sortedBy { it.second.pos.distanceTo(mc.player?.eyePos) }) {
                val hitResult = entity.second as EntityHitResult

                focusedEntities.add(entity.first to hitResult)
                event.rotation = RotationUtil.getRotations(mc.player?.eyePos!!, hitResult.pos).correctSensitivity()
            }
        }
        registerEvent(EventAttack::class.java, 1001) {
            if (closedInventory.value && mc.currentScreen != null) return@registerEvent

            for (focusedEntity in focusedEntities) {
                PlayerUtil.interact(focusedEntity.second)
                if (interactOnce.value && !interactedEntities.contains(focusedEntity.first))
                    interactedEntities.add(focusedEntity.first)
                timeUtil.reset()
            }
        }
        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.RECEIVE) {
                when (event.packet) {
                    is OpenScreenS2CPacket -> {
                        if (autoCloseScreens.value) {
                            mc.networkHandler?.sendPacket(CloseHandledScreenC2SPacket(event.packet.syncId))
                            CustomChat.printChatMessage("Auto closed a screen")
                            event.cancelled = true
                        }
                    }

                    is PlayerRespawnS2CPacket -> {
                        if (event.packet.isNewWorld()) {
                            onDisable()
                        }
                    }
                }
            }
        }
    }

}