package net.tarasandedevelopment.tarasande.features.module.player

import net.minecraft.entity.effect.StatusEffect
import net.minecraft.util.registry.Registry
import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.value.impl.ValueRegistry

class ModuleNoStatusEffect : Module("No status effect", "Cancels certain status effects.", ModuleCategory.PLAYER) {

    val effects = object : ValueRegistry<StatusEffect>(this, "Effects", Registry.STATUS_EFFECT) {
        override fun getTranslationKey(key: Any?) = Registry.STATUS_EFFECT.getId(key as StatusEffect?)!!.path
    }
}
