package de.florianmichael.tarasande_protocol_hack.util.extension

import de.florianmichael.vialoadingbase.api.version.ComparableProtocolVersion

operator fun ComparableProtocolVersion.compareTo(protocolVersion: ComparableProtocolVersion): Int {
    return protocolVersion.index - this.index
}
