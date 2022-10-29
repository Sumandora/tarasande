package net.tarasandedevelopment.tarasande.base.screen.cheatmenu.information

import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.cheatmenu.information.*

class ManagerInformation(val screenCheatMenu: ScreenCheatMenu) : Manager<Information>() {

    private fun conditional(condition: Boolean, block: () -> Information): Array<Information> {
        return (if (condition)
            listOf(block.invoke())
        else
            listOf()).toTypedArray()
    }

    init {
        add(
            // Player
            InformationName(),
            InformationXYZ(),
            InformationNetherXYZ(),
            InformationVelocity(),
            InformationRotation(),
            InformationFakeRotation(),

            // Connection
            InformationHandlers(),
            InformationProtocolVersion(),
            InformationPlayTime(),
            InformationViaPipeline(),

            // System
            InformationCPU(),
            InformationGPU(),
            *conditional(InformationPortage.isGenlopInstalled()) { InformationPortage() },
            InformationNowPlaying(),

            // World
            InformationEntities(),
            InformationWorldTime(),
            InformationSpawnPoint(),

            // Server
            InformationServerBrand(),
            InformationOpenChannels(),
            InformationVanishedPlayers(),

            // Modules
            InformationTimeShifted(),
            InformationSuspectedMurderers(),
            InformationFakeNewsCountdown(),
            InformationBeds(),
            InformationAntiAFKCountdown(),

            // Badlion
            InformationTimers(),

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
