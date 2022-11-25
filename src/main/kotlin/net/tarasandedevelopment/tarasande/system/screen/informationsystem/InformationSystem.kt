package net.tarasandedevelopment.tarasande.system.screen.informationsystem

import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.impl.*
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.panel.PanelInformation
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.ManagerPanel
import su.mandora.event.EventDispatcher

class ManagerInformation(panelSystem: ManagerPanel) : Manager<Information>() {

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

        EventDispatcher.add(EventSuccessfulLoad::class.java, 1001) {
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
