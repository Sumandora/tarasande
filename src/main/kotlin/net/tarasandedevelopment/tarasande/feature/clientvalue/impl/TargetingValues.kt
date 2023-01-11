package net.tarasandedevelopment.tarasande.feature.clientvalue.impl

import net.minecraft.entity.EntityType
import net.minecraft.entity.Tameable
import net.minecraft.registry.Registries
import net.minecraft.util.hit.EntityHitResult
import net.tarasandedevelopment.tarasande.event.EventDoAttack
import net.tarasandedevelopment.tarasande.event.EventIsEntityAttackable
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueRegistry
import net.tarasandedevelopment.tarasande.util.extension.mc
import net.tarasandedevelopment.tarasande.util.extension.minecraft.isEntityHitResult
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import su.mandora.event.EventDispatcher

object TargetingValues {
    val entities = object : ValueRegistry<EntityType<*>>(this, "Entities", Registries.ENTITY_TYPE, EntityType.PLAYER) {
        init {
            EventDispatcher.add(EventIsEntityAttackable::class.java) {
                it.attackable = it.attackable && list.contains(it.entity.type)
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
        object : ValueBoolean(this, "Don't attack riding entity", false) {
            init {
                EventDispatcher.add(EventIsEntityAttackable::class.java) {
                    if (value)
                        it.attackable = it.attackable && it.entity != mc.player?.vehicle
                }
            }

            override fun isEnabled() = entities.list.isNotEmpty()
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
}