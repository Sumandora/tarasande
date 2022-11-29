package de.florianmichael.tarasande_protocol_spoofer.multiplayerfeature.forgefaker.payload

import de.florianmichael.tarasande_protocol_spoofer.multiplayerfeature.forgefaker.payload.legacy.ModStruct

interface IForgePayload {

    fun installedMods(): List<ModStruct>
}
