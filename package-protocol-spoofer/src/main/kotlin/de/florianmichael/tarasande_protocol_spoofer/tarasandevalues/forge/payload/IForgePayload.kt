package de.florianmichael.tarasande_protocol_spoofer.tarasandevalues.forge.payload

import de.florianmichael.tarasande_protocol_spoofer.tarasandevalues.forge.payload.legacy.ModStruct

interface IForgePayload {

    fun installedMods(): List<ModStruct>
}
