package net.tarasandedevelopment.tarasande_windows_platform

import net.fabricmc.api.ClientModInitializer
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande_windows_platform.information.InformationWindowsSpotify
import net.tarasandedevelopment.tarasande_windows_platform.multiplayerfeature.MultiplayerFeatureWindowsFritzBoxReconnect
import net.tarasandedevelopment.tarasande_windows_platform.multiplayerfeature.MultiplayerFeatureWindowsTorNetwork
import su.mandora.event.EventDispatcher

class TarasandeWindowsPlatform : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            TarasandeMain.managerMultiplayerFeature().add(
                MultiplayerFeatureWindowsFritzBoxReconnect(),
                MultiplayerFeatureWindowsTorNetwork()
            )

            TarasandeMain.managerInformation().add(
                InformationWindowsSpotify()
            )
        }
    }
}
