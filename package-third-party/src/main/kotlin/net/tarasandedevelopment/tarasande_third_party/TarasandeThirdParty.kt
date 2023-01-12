package net.tarasandedevelopment.tarasande_third_party

import net.fabricmc.api.ClientModInitializer
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.ManagerInformation
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.ManagerPanel
import net.tarasandedevelopment.tarasande_third_party.information.InformationTimers
import net.tarasandedevelopment.tarasande_third_party.panel.PanelHypixelOverlay
import su.mandora.event.EventDispatcher

class TarasandeThirdParty : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            ManagerPanel.add(PanelHypixelOverlay())
            ManagerInformation.add(InformationTimers())
        }
    }
}
