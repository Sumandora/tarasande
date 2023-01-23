package de.florianmichael.tarasande_protocol_hack.definition.entitydimension

import de.florianmichael.tarasande_protocol_hack.definition.entitydimension.impl.DimensionWrapper1_12_2
import de.florianmichael.tarasande_protocol_hack.definition.entitydimension.impl.DimensionWrapperNative
import de.florianmichael.tarasande_protocol_hack.definition.entitydimension.wrapper.EntityDimensionWrapper
import de.florianmichael.tarasande_protocol_hack.util.values.ProtocolRange
import de.florianmichael.vialoadingbase.util.VersionListEnum

object EntityDimensionsDefinition {
    private val wrapperMap = HashMap<ProtocolRange, EntityDimensionWrapper>()
    var wrapper: EntityDimensionWrapper? = null

    init {
        wrapperMap[ProtocolRange(VersionListEnum.r1_12_2, VersionListEnum.r1_8)] = DimensionWrapper1_12_2()
    }

    fun reload(version: VersionListEnum) {
        for (entry in wrapperMap) {
            if (version in entry.key) {
                wrapper = entry.value
                println(version.getName() + " " + entry.value)
                return
            }
        }
        wrapper = DimensionWrapperNative()
    }
}
