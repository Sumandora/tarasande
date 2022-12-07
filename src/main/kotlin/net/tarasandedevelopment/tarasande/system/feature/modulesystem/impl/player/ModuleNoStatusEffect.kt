package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.entity.effect.StatusEffect
import net.minecraft.registry.Registries
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueRegistry
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleNoStatusEffect : Module("No status effect", "Cancels certain status effects.", ModuleCategory.PLAYER) {

    val effects = object : ValueRegistry<StatusEffect>(this, "Effects", Registries.STATUS_EFFECT) {
        override fun getTranslationKey(key: Any?) = (key as StatusEffect).translationKey
    }
}
