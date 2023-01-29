package de.florianmichael.tarasande_protocol_hack.definition.entitydimension

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.tarasande_protocol_hack.definition.entitydimension.impl.DimensionWrapper1_12_2
import de.florianmichael.tarasande_protocol_hack.definition.entitydimension.impl.DimensionWrapperNative
import de.florianmichael.tarasande_protocol_hack.definition.entitydimension.wrapper.EntityDimensionWrapper
import de.florianmichael.tarasande_protocol_hack.util.values.ProtocolRange
import de.florianmichael.vialoadingbase.api.version.ComparableProtocolVersion

object EntityDimensionsDefinition {
    private val wrapperMap = HashMap<ProtocolRange, EntityDimensionWrapper>()
    var wrapper: EntityDimensionWrapper = DimensionWrapperNative.get()

    init {
        wrapperMap[ProtocolRange(ProtocolVersion.v1_12_2, ProtocolVersion.v1_8)] = DimensionWrapper1_12_2()
    }

    fun reload(version: ComparableProtocolVersion) {
        for (entry in wrapperMap) {
            if (version in entry.key) {
                wrapper = entry.value
                return
            }
        }
        wrapper = DimensionWrapperNative.get()
    }
}
