package net.tarasandedevelopment.tarasande.system.screen.informationsystem.impl

import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information
import net.tarasandedevelopment.tarasande.util.extension.mc


class InformationTickDelta : Information("Game", "Tick delta") {

    override fun getMessage() = mc.tickDelta.toString()
}