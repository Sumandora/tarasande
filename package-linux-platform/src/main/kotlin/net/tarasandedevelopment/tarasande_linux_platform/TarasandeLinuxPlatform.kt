package net.tarasandedevelopment.tarasande_linux_platform

import net.fabricmc.api.ClientModInitializer
import net.minecraft.util.Util
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventShutdown
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande_linux_platform.information.InformationNowPlaying
import net.tarasandedevelopment.tarasande_linux_platform.information.InformationPortage
import su.mandora.event.EventDispatcher
import java.util.logging.Logger

class TarasandeLinuxPlatform : ClientModInitializer {
    private val logger = Logger.getLogger("tarasande-linux-platform")!!

    override fun onInitializeClient() {
        val operatingSystem = Util.getOperatingSystem()
        if(operatingSystem != Util.OperatingSystem.LINUX) {
            logger.warning("tarasande Linux Platform is not designed to run on '" + operatingSystem.getName() + "' systems")
            return
        }
        EventDispatcher.apply {
            add(EventSuccessfulLoad::class.java) {
                TarasandeMain.managerInformation().apply {
                    if (InformationPortage.isGenlopInstalled())
                        add(InformationPortage())
                    add(InformationNowPlaying())
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
