package de.florianmichael.tarasande_protocol_hack.util.extension

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.tarasande_protocol_hack.tarasande.values.ProtocolRange

operator fun ProtocolVersion.rangeTo(versionListEnum: ProtocolVersion): ProtocolRange {
    return ProtocolRange(this, versionListEnum)
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
