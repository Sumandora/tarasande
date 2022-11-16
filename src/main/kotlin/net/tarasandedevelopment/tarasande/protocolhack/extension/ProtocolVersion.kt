package net.tarasandedevelopment.tarasande.protocolhack.extension

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion

private val specialNames = object : HashMap<ProtocolVersion, String>() {
    init {
        this[ProtocolVersion.v1_9_3] = "1.9.3-1.9.4"
        this[ProtocolVersion.v1_11_1] = "1.11.1-1.11.2"
        this[ProtocolVersion.v1_16_4] = "1.16.4-1.16.5"
        this[ProtocolVersion.v1_18] = "1.18-1.18.1"
        this[ProtocolVersion.v1_19_1] = "1.19.1-1.19.2"
        this[ProtocolVersion.v1_19_3] = "22w46a"
    }
}

fun ProtocolVersion.getSpecialName() = specialNames.getOrDefault(this, this.name)