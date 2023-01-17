package de.florianmichael.tarasande_protocol_hack.util.extension

import de.florianmichael.vialoadingbase.util.VersionListEnum
import de.florianmichael.tarasande_protocol_hack.util.values.ProtocolRange

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
    return this.ordinal - versionListEnum.version
}
