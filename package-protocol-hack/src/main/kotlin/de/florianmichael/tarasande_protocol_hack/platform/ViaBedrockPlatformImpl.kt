package de.florianmichael.tarasande_protocol_hack.platform

import com.viaversion.viaversion.api.Via
import de.florianmichael.viabedrock.base.ViaBedrockPlatform
import de.florianmichael.vialoadingbase.util.JLoggerToLog4j
import org.apache.logging.log4j.LogManager
import java.io.File

class ViaBedrockPlatformImpl : ViaBedrockPlatform {
    private val logger = JLoggerToLog4j(LogManager.getLogger("ViaBedrock"))

    init {
        init()
    }

    override fun getLogger() = logger
    override fun getDataFolder(): File = Via.getPlatform().dataFolder
}
