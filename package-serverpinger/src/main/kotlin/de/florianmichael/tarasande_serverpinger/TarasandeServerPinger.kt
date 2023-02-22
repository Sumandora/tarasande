package de.florianmichael.tarasande_serverpinger

import de.florianmichael.tarasande_serverpinger.screenextension.ScreenExtensionGameMenuScreen
import de.florianmichael.tarasande_serverpinger.screenextension.directconnect.ScreenExtensionButtonListDirectConnect
import de.florianmichael.tarasande_serverpinger.screenextension.directconnect.ScreenExtensionDirectConnectScreen
import net.fabricmc.api.ClientModInitializer
import net.tarasandedevelopment.tarasande.event.impl.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension
import net.tarasandedevelopment.tarasande.event.EventDispatcher

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
