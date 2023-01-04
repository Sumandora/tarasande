package net.tarasandedevelopment.tarasande_protocol_hack.platform

import de.florianmichael.vialegacy.api.ViaLegacyPlatform
import de.florianmichael.vialoadingbase.util.JLoggerToLog4j
import org.apache.logging.log4j.LogManager

class ViaLegacyPlatformImpl : ViaLegacyPlatform {

    private val logger = JLoggerToLog4j(LogManager.getLogger("ViaLegacy"))

    init {
        init()
    }

    override fun getLogger() = logger
}
