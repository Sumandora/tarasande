package su.mandora.tarasande.system.screen.informationsystem

import su.mandora.tarasande.Manager
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventSuccessfulLoad
import su.mandora.tarasande.system.screen.informationsystem.impl.*
import su.mandora.tarasande.system.screen.informationsystem.panel.PanelInformation
import su.mandora.tarasande.system.screen.panelsystem.ManagerPanel

object ManagerInformation : Manager<Information>() {

    init {
        add(
            // Player
            InformationName(),
            InformationXYZ(),
            InformationNetherXYZ(),
            InformationVelocity(),
            InformationFallDistance(),
            InformationRotation(),
            InformationFakeRotation(),
            InformationReach(),

            // Connection
            InformationHandlers(),
            InformationPlayTime(),

            // World
            InformationEntities(),
            InformationWorldTime(),
            InformationSpawnPoint(),
            InformationTextRadar(),
            InformationVanishedPlayers(),
            InformationSequence(),

            // Server
            InformationServerBrand(),

            // Game
            InformationTickDelta()
        )

        EventDispatcher.add(EventSuccessfulLoad::class.java, 1001) {
            list.sortedBy { it.owner }
            ManagerPanel.add(PanelInformation(this))
        }
    }

    override fun insert(obj: Information, index: Int) {
        @Suppress("NAME_SHADOWING")
        var index = index
        // Is the owner known already? Move this information to the others from that owner
        val idx = list.indexOfLast { it.owner == obj.owner }
        if (idx != -1)
            index = idx + 1
        super.insert(obj, index)
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
