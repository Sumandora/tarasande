package su.mandora.tarasande.util.extension.kotlinruntime

import java.util.*

fun parseUUID(uuid: String): UUID {
    return if(uuid.contains("-"))
        UUID.fromString(uuid)
    else
        parseUUIDWithoutDashes(uuid)
}

fun parseUUIDWithoutDashes(uuid: String): UUID {
    val buf = StringBuffer(uuid)
    buf.insert(20, '-')
    buf.insert(16, '-')
    buf.insert(12, '-')
    buf.insert(8, '-')
    return UUID.fromString(buf.toString())
}