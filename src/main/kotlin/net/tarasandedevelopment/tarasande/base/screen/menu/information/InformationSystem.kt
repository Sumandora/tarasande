package net.tarasandedevelopment.tarasande.base.screen.menu.information

import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.screen.menu.information.*

class ManagerInformation : Manager<Information>() {

    init {
        add(
            // Connection
            InformationHandlers(),

            // World
            InformationWorldTime(),

            // Badlion
            InformationTimers(),

            // Modules
            InformationTimeShifted(),
            InformationSuspectedMurderers(),
            InformationFakeNewsCountdown(),
            InformationBeds()
        )
    }

    fun getAllOwners(): List<String> {
        return list.distinctBy { it.owner }.map { it.owner }
    }

    fun getAllInformation(owner: String): List<Information> {
        return list.filter { it.owner == owner }
    }

}

abstract class Information(val owner: String, val information: String) {
    abstract fun getMessage(): String?
}
