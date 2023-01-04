package net.tarasandedevelopment.tarasande_protocol_hack.util.extension

import de.florianmichael.vialoadingbase.util.VersionListEnum
import net.tarasandedevelopment.tarasande_protocol_hack.util.values.ProtocolRange
import kotlin.math.abs

operator fun VersionListEnum.rangeTo(versionListEnum: VersionListEnum): ProtocolRange {
    return ProtocolRange(this, versionListEnum)
}

fun VersionListEnum.andNewer(): ProtocolRange {
    return ProtocolRange(null, this)
}

fun VersionListEnum.andOlder(): ProtocolRange {
    return ProtocolRange(this, null)
}

fun VersionListEnum.singleton(): ProtocolRange {
    return ProtocolRange(this, this)
}

operator fun VersionListEnum.compareTo(versionListEnum: VersionListEnum): Int {
    // The lower bound is technically a higher version than the upper bound, we have to swap the operators
    // Also we have to respect certain protocols having negative ids

    if (version > 0 && versionListEnum.version < 0)
        return 1
    else if (version < 0 && versionListEnum.version > 0)
        return -1

    return abs(this.version) - abs(versionListEnum.version)
}
