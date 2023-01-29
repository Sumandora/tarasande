package de.florianmichael.tarasande_protocol_hack.platform

import de.florianmichael.vialoadingbase.util.JLoggerToLog4j
import de.florianmichael.viasnapshot.base.ViaSnapshotPlatform
import org.apache.logging.log4j.LogManager

class ViaSnapshotPlatformImpl : ViaSnapshotPlatform {

    private val logger = JLoggerToLog4j(LogManager.getLogger("ViaCursed"))

    init {
        init()
    }

    override fun getLogger() = logger
}
