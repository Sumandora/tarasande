package su.mandora.tarasande_serverpinger

import su.mandora.tarasande_serverpinger.screenextension.ScreenExtensionGameMenuScreen
import su.mandora.tarasande_serverpinger.screenextension.directconnect.ScreenExtensionButtonListDirectConnect
import su.mandora.tarasande_serverpinger.screenextension.directconnect.ScreenExtensionDirectConnectScreen
import net.fabricmc.api.ClientModInitializer
import su.mandora.tarasande.event.impl.EventSuccessfulLoad
import su.mandora.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension
import su.mandora.tarasande.event.EventDispatcher

class TarasandeServerPinger : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            ManagerScreenExtension.add(
                ScreenExtensionDirectConnectScreen(), ScreenExtensionButtonListDirectConnect(),

                ScreenExtensionGameMenuScreen()
            )
        }
    }
}
