package net.tarasandedevelopment.tarasande.protocolhack.fix

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.protocolhack.util.ProtocolRange
import net.tarasandedevelopment.tarasande.util.extension.andOlder

object EntityDimensionReplacement {

    private val replacements = HashMap<EntityType<*>, MutableList<Pair<EntityDimensions, ProtocolRange>>>()
    private val localReplacements = HashMap<EntityType<*>, EntityDimensions>()

    init {
        registerReplacement(EntityType.WITHER_SKELETON, EntityDimensions.changing(0.72F, 2.535F), ProtocolVersion.v1_8.andOlder())
    }

    fun reloadDimensions() {
        localReplacements.clear()
        replacements.forEach {
            for (entityDimensionsProtocolRangePair in it.value) {
                if (ProtocolVersion.getProtocol(TarasandeMain.get().protocolHack.clientsideVersion) in entityDimensionsProtocolRangePair.second) {
                    localReplacements[it.key] = entityDimensionsProtocolRangePair.first
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
