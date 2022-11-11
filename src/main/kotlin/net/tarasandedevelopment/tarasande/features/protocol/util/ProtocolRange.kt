package net.tarasandedevelopment.tarasande.features.protocol.util

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import net.tarasandedevelopment.tarasande.features.protocol.extension.getSpecialName
import net.tarasandedevelopment.tarasande.util.extension.compareTo

class ProtocolRange(private val lowerBound: ProtocolVersion?, private val upperBound: ProtocolVersion?) {

    init {
        if (lowerBound == null && upperBound == null)
            error("Invalid protocol range")
    }

    operator fun contains(protocolVersion: ProtocolVersion): Boolean {
        if (lowerBound != null && lowerBound < protocolVersion)
            return false
        if (upperBound != null && upperBound > protocolVersion)
            return false
        return true
    }

    override fun toString(): String {
        return when {
            lowerBound == null -> upperBound!!.getSpecialName() + "+"
            upperBound == null -> lowerBound.getSpecialName() + "-"
            lowerBound == upperBound -> lowerBound.getSpecialName()
            else -> lowerBound.getSpecialName() + " - " + upperBound.getSpecialName()
        }
    }
}
