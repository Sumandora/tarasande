package net.tarasandedevelopment.tarasande.util.extension

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import net.tarasandedevelopment.tarasande.features.protocol.util.ProtocolRange
import kotlin.math.abs

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
