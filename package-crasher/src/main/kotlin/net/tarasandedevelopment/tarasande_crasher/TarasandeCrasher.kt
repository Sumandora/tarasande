package net.tarasandedevelopment.tarasande_crasher

import net.fabricmc.api.ClientModInitializer
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension
import net.tarasandedevelopment.tarasande_crasher.screenextension.ScreenExtensionButtonListDirectConnect
import su.mandora.event.EventDispatcher

class TarasandeCrasher : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            ManagerScreenExtension.add(
                ScreenExtensionButtonListDirectConnect()
            )
        }
    }
}
