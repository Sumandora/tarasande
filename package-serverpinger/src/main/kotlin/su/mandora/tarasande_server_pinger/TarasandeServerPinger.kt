package su.mandora.tarasande_server_pinger

import net.fabricmc.api.ClientModInitializer
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventSuccessfulLoad
import su.mandora.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension
import su.mandora.tarasande_server_pinger.screenextension.ScreenExtensionGameMenuScreen
import su.mandora.tarasande_server_pinger.screenextension.directconnect.ScreenExtensionDirectConnectScreen

class TarasandeServerPinger : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            ManagerScreenExtension.add(
                ScreenExtensionDirectConnectScreen(),
                ScreenExtensionGameMenuScreen()
            )
        }
    }
}
