package net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.payload

import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.payload.legacy.ModStruct

interface IForgePayload {

    fun installedMods(): List<ModStruct>
}
