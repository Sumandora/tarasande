package su.mandora.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.entity.effect.StatusEffect
import net.minecraft.registry.Registries
import su.mandora.tarasande.system.base.valuesystem.impl.ValueRegistry
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleNoStatusEffect : Module("No status effect", "Cancels undesired status effects", ModuleCategory.PLAYER) {

    val effects = object : ValueRegistry<StatusEffect>(this, "Effects", Registries.STATUS_EFFECT, true) {
        override fun getTranslationKey(key: Any?) = (key as StatusEffect).translationKey
    }
}
