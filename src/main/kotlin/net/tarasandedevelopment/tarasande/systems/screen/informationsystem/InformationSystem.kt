package net.tarasandedevelopment.tarasande.systems.screen.informationsystem

import net.tarasandedevelopment.event.EventDispatcher
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.events.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.systems.screen.informationsystem.impl.*
import net.tarasandedevelopment.tarasande.systems.screen.informationsystem.panel.PanelInformation
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.ManagerPanel

class ManagerInformation(panelSystem: ManagerPanel) : Manager<Information>() {

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
            InformationPlayTime(),

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

            // Private message detector
            InformationDetectedMessages(),

            // Badlion
            InformationTimers()
        )

        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            list.sortedBy { it.owner }
            panelSystem.add(PanelInformation(this))
        }
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
