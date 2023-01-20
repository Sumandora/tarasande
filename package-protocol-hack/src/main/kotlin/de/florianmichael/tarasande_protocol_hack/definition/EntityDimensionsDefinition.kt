package de.florianmichael.tarasande_protocol_hack.definition

import de.florianmichael.vialoadingbase.ViaLoadingBase
import de.florianmichael.vialoadingbase.util.VersionListEnum
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import de.florianmichael.tarasande_protocol_hack.util.extension.andOlder
import de.florianmichael.tarasande_protocol_hack.util.values.ProtocolRange

object EntityDimensionsDefinition {

    private val replacements = HashMap<EntityType<*>, MutableList<Pair<EntityDimensions, ProtocolRange>>>()
    private val localReplacements = HashMap<EntityType<*>, EntityDimensions>()

    init {
        registerReplacement(EntityType.WITHER_SKELETON, EntityDimensions.changing(0.72F, 2.535F), VersionListEnum.r1_8.andOlder())

        registerReplacement(EntityType.SKELETON_HORSE, EntityDimensions.changing(1.4F, 2.535F), VersionListEnum.r1_8.andOlder())
        registerReplacement(EntityType.ZOMBIE_HORSE, EntityDimensions.changing(1.4F, 2.535F), VersionListEnum.r1_8.andOlder())
        registerReplacement(EntityType.HORSE, EntityDimensions.changing(1.4F, 2.535F), VersionListEnum.r1_8.andOlder())

        registerReplacement(EntityType.BOAT, EntityDimensions.changing(1.375F, 2.535F), VersionListEnum.r1_8.andOlder())

        registerReplacement(EntityType.RABBIT, EntityDimensions.changing(0.6F, 2.535F), VersionListEnum.r1_8.andOlder())
    }

    fun reloadDimensions() {
        localReplacements.clear()
        replacements.forEach {
            for (pair in it.value) {
                if (ViaLoadingBase.getTargetVersion() in pair.second) {
                    localReplacements[it.key] = pair.first
                }
            }
        }
    }

    fun wrapDimension(type: EntityType<*>): EntityDimensions? {
        if (localReplacements.containsKey(type)) {
            return localReplacements[type]
        }
        return null
    }

    private fun registerReplacement(type: EntityType<*>, dimensions: EntityDimensions, range: ProtocolRange) {
        if (replacements.containsKey(type)) {
            replacements[type]?.add(Pair(dimensions, range))
        } else {
            replacements[type] = mutableListOf(Pair(dimensions, range))
        }
    }
}
