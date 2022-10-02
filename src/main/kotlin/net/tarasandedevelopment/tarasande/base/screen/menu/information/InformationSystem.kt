package net.tarasandedevelopment.tarasande.base.screen.menu.information

import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.screen.menu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.menu.information.*

class ManagerInformation(val screenCheatMenu: ScreenCheatMenu) : Manager<Information>() {

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
            InformationSpawnPoint(),

            // Badlion
            InformationTimers(),

            // Server
            InformationServerBrand(),
            InformationProtocolVersion(),

            // Modules
            InformationTimeShifted(),
            InformationSuspectedMurderers(),
            InformationFakeNewsCountdown(),

            // Graphs
            *screenCheatMenu.managerGraph.list.map { e -> InformationGraphValue(e) }.toTypedArray(),

            // Beds
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
