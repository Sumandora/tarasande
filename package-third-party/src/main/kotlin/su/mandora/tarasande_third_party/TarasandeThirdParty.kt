package su.mandora.tarasande_third_party

import net.fabricmc.api.ClientModInitializer
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.feature.clientvalue.ClientValues
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.ManagerInformation
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.ManagerPanel
import su.mandora.tarasande_third_party.information.InformationTimers
import su.mandora.tarasande_third_party.panel.PanelHypixelOverlay
import su.mandora.event.EventDispatcher
import su.mandora.tarasande_third_party.discord.DiscordValues

class TarasandeThirdParty : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            ManagerPanel.add(PanelHypixelOverlay())
            ManagerInformation.add(InformationTimers())

            ValueButtonOwnerValues(ClientValues, "Discord values", DiscordValues)
        }
    }
}
