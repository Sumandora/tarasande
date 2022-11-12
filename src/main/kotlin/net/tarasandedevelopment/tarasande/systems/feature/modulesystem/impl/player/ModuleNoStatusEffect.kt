package net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.player

import net.minecraft.entity.effect.StatusEffect
import net.minecraft.util.registry.Registry
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueRegistry
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ModuleCategory

class ModuleNoStatusEffect : Module("No status effect", "Cancels certain status effects.", ModuleCategory.PLAYER) {

    val effects = object : ValueRegistry<StatusEffect>(this, "Effects", Registry.STATUS_EFFECT) {
        override fun getTranslationKey(key: Any?) = Registry.STATUS_EFFECT.getId(key as StatusEffect?)!!.path
    }
}
