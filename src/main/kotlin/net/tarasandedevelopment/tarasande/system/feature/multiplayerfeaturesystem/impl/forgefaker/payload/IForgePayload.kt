package net.tarasandedevelopment.tarasande.system.feature.multiplayerfeaturesystem.impl.forgefaker.payload

import net.tarasandedevelopment.tarasande.system.feature.multiplayerfeaturesystem.impl.forgefaker.payload.legacy.ModStruct

interface IForgePayload {

    fun installedMods(): List<ModStruct>
}
