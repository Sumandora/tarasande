package net.tarasandedevelopment.tarasande_crasher

import net.fabricmc.api.ClientModInitializer
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande_crasher.crasher.ManagerCrasher
import net.tarasandedevelopment.tarasande_crasher.screenextension.ScreenExtensionButtonListDirectConnect
import su.mandora.event.EventDispatcher

class TarasandeCrasher : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            TarasandeMain.managerScreenExtension().add(
                ScreenExtensionButtonListDirectConnect(ManagerCrasher())
            )
        }
    }
}
