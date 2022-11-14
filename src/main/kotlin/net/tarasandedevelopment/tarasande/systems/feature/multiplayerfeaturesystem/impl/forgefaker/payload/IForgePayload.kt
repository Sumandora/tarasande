package net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.impl.forgefaker.payload

import net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.impl.forgefaker.payload.legacy.ModStruct

interface IForgePayload {

    fun installedMods(): List<ModStruct>
}
