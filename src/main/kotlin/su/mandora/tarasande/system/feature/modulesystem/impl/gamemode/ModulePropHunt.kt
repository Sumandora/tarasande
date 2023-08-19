package su.mandora.tarasande.system.feature.modulesystem.impl.gamemode

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket
import net.minecraft.network.packet.s2c.play.EntityS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
import net.minecraft.registry.Registries
import su.mandora.tarasande.event.impl.EventEntityColor
import su.mandora.tarasande.event.impl.EventIsEntityAttackable
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueColor
import su.mandora.tarasande.system.base.valuesystem.impl.ValueRegistry
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.extension.kotlinruntime.WeakSet
import su.mandora.tarasande.util.extension.minecraft.packet.isNewWorld

class ModulePropHunt : Module("Prop hunt", "Shows moving entities", ModuleCategory.GAMEMODE) {

    private val entities = object : ValueRegistry<EntityType<*>>(this, "Entities", Registries.ENTITY_TYPE, true, EntityType.PLAYER) {
        override fun getTranslationKey(key: Any?) = (key as EntityType<*>).translationKey
        override fun onAdd(key: EntityType<*>) {
            movedEntities.clear()
        }

        override fun onRemove(key: EntityType<*>) {
            movedEntities.clear()
        }
    }
    private val colorOverride = ValueColor(this, "Color override", 0.0, 1.0, 1.0, 1.0)

    private var movedEntities = WeakSet<Entity>()

    override fun onDisable() {
        movedEntities = WeakSet()
    }

    private fun getEntity(p: Packet<*>): Entity? {
        return when (p) {
            is EntityS2CPacket -> p.getEntity(mc.world)
            is EntityPositionS2CPacket -> mc.world?.getEntityById(p.id)
            else -> null
        }
    }

    private fun isMoved(p: Packet<*>, entity: Entity): Boolean {
        return when (p) {
            is EntityS2CPacket -> p.deltaX != 0.toShort() || p.deltaY != 0.toShort() || p.deltaZ != 0.toShort()
            is EntityPositionS2CPacket -> p.x != entity.trackedPosition.pos.x || p.y != entity.trackedPosition.pos.y || p.z != entity.trackedPosition.pos.z
            else -> false
        }
    }

    init {
        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.RECEIVE)
                when (event.packet) {
                    is EntityS2CPacket, is EntityPositionS2CPacket -> {
                        val entity = getEntity(event.packet)
                        if(entity != null && entities.isSelected(entity.type) && isMoved(event.packet, entity)) {
                            movedEntities.add(entity)
                        }
                    }
                    is PlayerRespawnS2CPacket -> {
                        if(event.packet.isNewWorld())
                            movedEntities.clear()
                    }
                }
        }

        registerEvent(EventIsEntityAttackable::class.java) { event ->
            if(event.attackable)
                if(!movedEntities.contains(event.entity))
                    event.attackable = false
        }

        registerEvent(EventEntityColor::class.java) { event ->
            if(movedEntities.contains(event.entity))
                event.color = colorOverride.getColor()
        }
    }

}