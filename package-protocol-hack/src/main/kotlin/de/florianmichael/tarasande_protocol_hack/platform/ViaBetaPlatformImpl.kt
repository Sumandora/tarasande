package de.florianmichael.tarasande_protocol_hack.platform

import com.viaversion.viaversion.api.Via
import de.florianmichael.viabeta.base.ViaBetaPlatform
import de.florianmichael.vialoadingbase.ViaLoadingBase
import de.florianmichael.vialoadingbase.util.JLoggerToLog4j
import org.apache.logging.log4j.LogManager
import java.io.File

class ViaBetaPlatformImpl : ViaBetaPlatform {

    private val logger = JLoggerToLog4j(LogManager.getLogger("ViaBeta"))

    init {
        init()
    }

    override fun getLogger() = logger
    override fun getDataFolder(): File = Via.getPlatform().dataFolder
}
