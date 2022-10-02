package net.tarasandedevelopment.tarasande.base.screen.menu.information

import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.screen.menu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.menu.information.*

class ManagerInformation(val screenCheatMenu: ScreenCheatMenu) : Manager<Information>() {

    init {
        add(
            // Connection
            InformationHandlers(),

            // Badlion
            InformationTimers(),

            // Modules
            InformationTimeShifted(),
            InformationSuspectedMurderers(),
            InformationFakeNewsCountdown()
        )

        informationList.forEach {
            add(it)
        }

        for (graph in screenCheatMenu.managerGraph.list) {
            add(InformationGraphValue(graph))
        }

        add(InformationBeds())
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
