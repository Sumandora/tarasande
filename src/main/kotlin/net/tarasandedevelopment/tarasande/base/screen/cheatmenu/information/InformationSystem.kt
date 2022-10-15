package net.tarasandedevelopment.tarasande.base.screen.cheatmenu.information

import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.cheatmenu.information.*

class ManagerInformation(val screenCheatMenu: ScreenCheatMenu) : Manager<Information>() {

    init {
        add(
            // Player
            InformationName(),
            InformationXYZ(),
            InformationNetherXYZ(),
            InformationVelocity(),
            InformationRotation(),
            InformationFakeRotation(),

            // Time
            InformationDate(),
            InformationTime(),

            // Connection
            InformationHandlers(),
            InformationProtocolVersion(),
            InformationPlayTime(),

            // Hardware
            InformationCPU(),
            InformationGPU(),

            // World
            InformationEntities(),
            InformationWorldTime(),
            InformationSpawnPoint(),

            // Badlion
            InformationTimers(),

            // Server
            InformationServerBrand(),
            InformationOpenChannels(),

            // Features
            InformationFeaturesModules(),
            InformationFeaturesProtocols(),
            InformationFeaturesCreativeItems(),
            InformationFeaturesValues(),
            InformationFeaturesGraphs(),

            // Modules
            InformationTimeShifted(),
            InformationSuspectedMurderers(),
            InformationFakeNewsCountdown(),
            InformationBeds(),
            InformationAntiAFKCountdown(),

            // Graphs
            *screenCheatMenu.managerGraph.list.map { e -> InformationGraphValue(e) }.toTypedArray()
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
