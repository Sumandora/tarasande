package net.tarasandedevelopment.tarasande_protocol_hack.platform

import de.florianmichael.vialoadingbase.ViaLoadingBase
import de.florianmichael.vialoadingbase.util.JLoggerToLog4j
import net.raphimc.vialegacy.platform.ViaLegacyPlatform
import org.apache.logging.log4j.LogManager

class ViaLegacyPlatformImpl : ViaLegacyPlatform {

    private val logger = JLoggerToLog4j(LogManager.getLogger("ViaLegacy"))

    init {
        init(dataFolder)
    }

    override fun getLogger() = logger
    override fun getDataFolder() = ViaLoadingBase.instance().directory()!!
}
