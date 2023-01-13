package net.tarasandedevelopment.tarasande.system.screen.informationsystem.impl

import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information


class InformationTickDelta : Information("Game", "Tick delta") {

    override fun getMessage() = mc.tickDelta.toString()
}