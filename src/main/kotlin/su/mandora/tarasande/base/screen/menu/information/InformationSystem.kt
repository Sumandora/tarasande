package su.mandora.tarasande.base.screen.menu.information

import su.mandora.tarasande.base.Manager
import su.mandora.tarasande.screen.menu.information.*

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
