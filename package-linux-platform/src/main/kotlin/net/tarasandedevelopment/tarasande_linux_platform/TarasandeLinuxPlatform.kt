package net.tarasandedevelopment.tarasande_linux_platform

import net.fabricmc.api.ClientModInitializer
import net.tarasandedevelopment.tarasande_linux_platform.information.InformationNowPlaying
import net.tarasandedevelopment.tarasande_linux_platform.information.InformationPortage
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventShutdown
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import su.mandora.event.EventDispatcher

class TarasandeLinuxPlatform : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.apply {
            add(EventSuccessfulLoad::class.java) {
                TarasandeMain.managerInformation().apply {
                    if (InformationPortage.isGenlopInstalled())
                        TarasandeMain.managerInformation().add(InformationPortage())
                    TarasandeMain.managerInformation().add(InformationNowPlaying())
                }
                try {
                    Runtime.getRuntime().exec("qdbus org.kde.KWin /Compositor suspend")
                } catch (ignored: Throwable) {
                }
            }
            add(EventShutdown::class.java) {
                try {
                    Runtime.getRuntime().exec("qdbus org.kde.KWin /Compositor resume")
                } catch (ignored: Throwable) {
                }
            }
        }
    }

}
