package net.tarasandedevelopment.tarasande.systems.screen.clientmenu.clientmenu.forgefaker.payload

import net.tarasandedevelopment.tarasande.systems.screen.clientmenu.clientmenu.forgefaker.payload.legacy.ModStruct

interface IForgePayload {

    fun installedMods(): List<ModStruct>
}
