package su.mandora.tarasande_linux_platform

import net.fabricmc.api.ClientModInitializer
import net.minecraft.util.Util
import su.mandora.tarasande.TARASANDE_NAME
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventSuccessfulLoad
import su.mandora.tarasande.system.screen.informationsystem.ManagerInformation
import su.mandora.tarasande_linux_platform.information.InformationNowPlaying
import su.mandora.tarasande_linux_platform.information.InformationPortage
import java.util.logging.Logger

class TarasandeLinuxPlatform : ClientModInitializer {
    private val logger = Logger.getLogger("$TARASANDE_NAME-linux-platform")!!

    override fun onInitializeClient() {
        val operatingSystem = Util.getOperatingSystem()
        if (operatingSystem != Util.OperatingSystem.LINUX) {
            logger.warning("$TARASANDE_NAME Linux platform is not designed to run on '" + operatingSystem.getName() + "' systems")
            return
        }

        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            ManagerInformation.apply {
                if(InformationPortage.isGenlopInstalled())
                    add(InformationPortage())
                add(InformationNowPlaying())
            }
        }
    }
}
