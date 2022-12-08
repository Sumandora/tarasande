package net.tarasandedevelopment.tarasande_protocol_hack.extension

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import net.tarasandedevelopment.tarasande_protocol_hack.util.ProtocolRange
import kotlin.math.abs

private val specialNames = object : HashMap<ProtocolVersion, String>() {
    init {
        this[ProtocolVersion.v1_9_3] = "1.9.3-1.9.4"
        this[ProtocolVersion.v1_11_1] = "1.11.1-1.11.2"
        this[ProtocolVersion.v1_16_4] = "1.16.4-1.16.5"
        this[ProtocolVersion.v1_18] = "1.18-1.18.1"
        this[ProtocolVersion.v1_19_1] = "1.19.1-1.19.2"
    }
}

fun ProtocolVersion.getSpecialName() = specialNames.getOrDefault(this, this.name)

operator fun ProtocolVersion.rangeTo(protocolVersion: ProtocolVersion): ProtocolRange {
    return ProtocolRange(this, protocolVersion)
}

fun ProtocolVersion.andNewer(): ProtocolRange {
    return ProtocolRange(null, this)
}

fun ProtocolVersion.andOlder(): ProtocolRange {
    return ProtocolRange(this, null)
}

fun ProtocolVersion.singleton(): ProtocolRange {
    return ProtocolRange(this, this)
}

operator fun ProtocolVersion.compareTo(protocolVersion: ProtocolVersion): Int {
    // The lower bound is technically a higher version than the upper bound, we have to swap the operators
    // Also we have to respect certain protocols having negative ids

    if (version > 0 && protocolVersion.version < 0)
        return 1
    else if (version < 0 && protocolVersion.version > 0)
        return -1

    return abs(this.version) - abs(protocolVersion.version)
}
