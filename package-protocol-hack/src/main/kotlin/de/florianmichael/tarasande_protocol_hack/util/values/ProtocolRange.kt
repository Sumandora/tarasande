package de.florianmichael.tarasande_protocol_hack.util.values

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.tarasande_protocol_hack.util.extension.andNewer
import de.florianmichael.tarasande_protocol_hack.util.extension.andOlder
import de.florianmichael.tarasande_protocol_hack.util.extension.compareTo
import de.florianmichael.vialoadingbase.api.version.ComparableProtocolVersion
import de.florianmichael.vialoadingbase.api.version.InternalProtocolList

fun formatRange(vararg version: ProtocolRange) = version.joinToString(", ") { it.toString() }

class ProtocolRange(private val lowerBound: ProtocolVersion?, private val upperBound: ProtocolVersion?) {

    private lateinit var lowerBoundComparable: ComparableProtocolVersion
    private lateinit var upperBoundComparable: ComparableProtocolVersion

    init {
        if (lowerBound == null && upperBound == null) {
            error("Invalid protocol range")
        }

        if (lowerBound != null) lowerBoundComparable = InternalProtocolList.fromProtocolVersion(lowerBound)
        if (upperBound != null) upperBoundComparable = InternalProtocolList.fromProtocolVersion(upperBound)
    }

    operator fun contains(protocolVersion: ComparableProtocolVersion): Boolean {
        if (lowerBound != null && lowerBoundComparable < protocolVersion)
            return false
        if (upperBound != null && upperBoundComparable > protocolVersion)
            return false
        return true
    }

    override fun toString(): String {
        return when {
            lowerBound == null -> upperBound!!.name + "+"
            upperBound == null -> lowerBound.name + "-"
            lowerBound == upperBound -> lowerBound.name
            else -> lowerBound.name + " - " + upperBound.name
        }
    }

    fun inverse(): Array<ProtocolRange> {
        return listOfNotNull(
            lowerBound?.andNewer(),
            upperBound?.andOlder()
        ).toTypedArray()
    }
}
