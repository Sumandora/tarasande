package de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.payload

import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.payload.legacy.ModStruct

interface IForgePayload {

    fun installedMods(): List<ModStruct>
}
