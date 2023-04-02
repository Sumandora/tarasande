package su.mandora.tarasande_third_party

import net.fabricmc.api.ClientModInitializer
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventSuccessfulLoad
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues
import su.mandora.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import su.mandora.tarasande.system.screen.informationsystem.ManagerInformation
import su.mandora.tarasande.system.screen.panelsystem.ManagerPanel
import su.mandora.tarasande_third_party.discord.DiscordValues
import su.mandora.tarasande_third_party.information.InformationTimers
import su.mandora.tarasande_third_party.panel.PanelHypixelOverlay

class TarasandeThirdParty : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            ManagerPanel.add(PanelHypixelOverlay())
            ManagerInformation.add(InformationTimers())

            ValueButtonOwnerValues(TarasandeValues, "Discord values", DiscordValues)
        }
    }
}
