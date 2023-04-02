package su.mandora.tarasande.system.screen.informationsystem.impl

import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.informationsystem.Information


class InformationTickDelta : Information("Game", "Tick delta") {

    override fun getMessage() = mc.tickDelta.toString()
}