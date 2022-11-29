package de.florianmichael.tarasande_protocol_spoofer

import de.florianmichael.tarasande_protocol_spoofer.multiplayerfeature.MultiplayerFeatureToggleableExploitsBungeeHack
import de.florianmichael.tarasande_protocol_spoofer.multiplayerfeature.MultiplayerFeatureToggleableExploitsForgeFaker
import de.florianmichael.tarasande_protocol_spoofer.multiplayerfeature.MultiplayerFeatureToggleableExploitsHAProxyHack
import de.florianmichael.tarasande_protocol_spoofer.multiplayerfeature.MultiplayerFeatureToggleableExploitsQuiltFaker
import net.fabricmc.api.ClientModInitializer
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import su.mandora.event.EventDispatcher

class TarasandeProtocolSpoofer : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            TarasandeMain.managerMultiplayerFeature().add(
                MultiplayerFeatureToggleableExploitsBungeeHack(),
                MultiplayerFeatureToggleableExploitsForgeFaker(),
                MultiplayerFeatureToggleableExploitsHAProxyHack(),
                MultiplayerFeatureToggleableExploitsQuiltFaker(),
            )
        }
    }
}
