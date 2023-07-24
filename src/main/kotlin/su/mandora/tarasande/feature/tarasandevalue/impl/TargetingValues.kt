package su.mandora.tarasande.feature.tarasandevalue.impl

import net.minecraft.entity.EntityType
import net.minecraft.entity.Tameable
import net.minecraft.registry.Registries
import net.minecraft.util.hit.EntityHitResult
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventDoAttack
import su.mandora.tarasande.event.impl.EventIsEntityAttackable
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueRegistry
import su.mandora.tarasande.util.extension.minecraft.isEntityHitResult
import su.mandora.tarasande.util.player.PlayerUtil

object TargetingValues {
    val entities = object : ValueRegistry<EntityType<*>>(this, "Entities", Registries.ENTITY_TYPE, true, EntityType.PLAYER) {
        init {
            EventDispatcher.add(EventIsEntityAttackable::class.java) {
                it.attackable = it.attackable && isSelected(it.entity.type)
            }
        }

        override fun getTranslationKey(key: Any?) = (key as EntityType<*>).translationKey
    }

    init {
        object : ValueBoolean(this, "Don't attack tamed entities", false) {
            init {
                EventDispatcher.add(EventIsEntityAttackable::class.java) {
                    if (value)
                        it.attackable = it.attackable && (it.entity !is Tameable || it.entity.ownerUuid != mc.player?.uuid)
                }
            }
        }
        object : ValueBoolean(this, "Don't attack riding entity", false, isEnabled = { entities.anySelected() }) {
            init {
                EventDispatcher.add(EventIsEntityAttackable::class.java) {
                    if (value)
                        it.attackable = it.attackable && it.entity != mc.player?.vehicle
                }
            }
        }
        object : ValueBoolean(this, "Prevent hitting of invalid entities", false) {
            init {
                EventDispatcher.add(EventDoAttack::class.java) {
                    if (!value)
                        return@add

                    if (mc.crosshairTarget.isEntityHitResult()) {
                        val entity = (mc.crosshairTarget as EntityHitResult).entity
                        if (!PlayerUtil.isAttackable(entity))
                            it.cancelled = true
                    }
                }
            }
        }
    }

    val closedInventory = ValueBoolean(this, "Closed inventory", false)

}