package net.tarasandedevelopment.tarasande_protocol_hack.platform

import de.florianmichael.viacursed.base.ViaCursedPlatform
import de.florianmichael.vialoadingbase.util.JLoggerToLog4j
import org.apache.logging.log4j.LogManager

class ViaCursedPlatformImpl : ViaCursedPlatform {

    private val logger = JLoggerToLog4j(LogManager.getLogger("ViaCursed"))

    init {
        init()
    }

    override fun getLogger() = logger
}
