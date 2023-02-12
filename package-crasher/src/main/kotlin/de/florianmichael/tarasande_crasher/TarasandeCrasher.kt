package de.florianmichael.tarasande_crasher

import de.florianmichael.tarasande_crasher.module.*
import net.fabricmc.api.ClientModInitializer
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension
import de.florianmichael.tarasande_crasher.screenextension.ScreenExtensionButtonListDirectConnect
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule
import su.mandora.event.EventDispatcher

class TarasandeCrasher : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            ManagerScreenExtension.add(
                ScreenExtensionButtonListDirectConnect()
            )

            ManagerModule.add(
                ModuleBoatCrasher(),
                ModuleOffHandCrasher(),
                ModuleZeroSmasher(),
                ModuleSinglePacketCrasher(),
                ModuleFlyCrasher()
            )
        }
    }
}
