package net.tarasandedevelopment.tarasande.feature.clientvalue.impl

import net.minecraft.entity.EntityType
import net.minecraft.entity.Tameable
import net.minecraft.registry.Registries
import net.tarasandedevelopment.tarasande.event.EventIsEntityAttackable
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueRegistry
import net.tarasandedevelopment.tarasande.util.extension.mc
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
    }
}