package su.mandora.tarasande.base.screen.menu.information

import su.mandora.tarasande.base.Manager
import su.mandora.tarasande.screen.menu.information.*

class ManagerInformation : Manager<Information>() {

    init {
        add(
            // Player
            InformationXYZ(),
            InformationNetherXYZ(),
            InformationRotation(),
            InformationFakeRotation(),

            // Time
            InformationDate(),
            InformationTime(),

            // Connection
            InformationHandlers(),

            // World
            InformationEntities(),
            InformationWorldTime(),

            // Badlion
            InformationTimers(),

            // Modules
            InformationTimeShifted(),
            InformationMurderer(),
            InformationBeds()
        )
    }

    fun getAllOwners(): List<String> {
        return list.filter { it.isVisible() }.distinctBy { it.owner }.map { it.owner }
    }

    fun getAllInformation(owner: String): List<Information> {
        return list.filter { it.isVisible() }.filter { it.owner == owner }
    }

}

abstract class Information(val owner: String, val information: String) {
    fun isVisible() = true
    abstract fun getMessage(): String?
}
