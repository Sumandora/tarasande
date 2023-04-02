package su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.payload

import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.payload.legacy.ModStruct

interface IForgePayload {

    fun installedMods(): List<ModStruct>
}
