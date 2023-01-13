package net.tarasandedevelopment.tarasande_protocol_hack.util.values

import de.florianmichael.vialoadingbase.util.VersionListEnum
import net.tarasandedevelopment.tarasande_protocol_hack.util.extension.andNewer
import net.tarasandedevelopment.tarasande_protocol_hack.util.extension.andOlder

fun formatRange(vararg version: ProtocolRange) = version.joinToString(", ") { it.toString() }

class ProtocolRange(private val lowerBound: VersionListEnum?, private val upperBound: VersionListEnum?) {

    init {
        if (lowerBound == null && upperBound == null)
            error("Invalid protocol range")
    }

    operator fun contains(protocolVersion: VersionListEnum): Boolean {
        if (lowerBound != null && lowerBound < protocolVersion)
            return false
        if (upperBound != null && upperBound > protocolVersion)
            return false
        return true
    }

    override fun toString(): String {
        return when {
            lowerBound == null -> upperBound!!.getName() + "+"
            upperBound == null -> lowerBound.getName() + "-"
            lowerBound == upperBound -> lowerBound.getName()
            else -> lowerBound.getName() + " - " + upperBound.getName()
        }
    }

    fun inverse(): Array<ProtocolRange> {
        return listOfNotNull(
            lowerBound?.andNewer(),
            upperBound?.andOlder()
        ).toTypedArray()
    }
}
