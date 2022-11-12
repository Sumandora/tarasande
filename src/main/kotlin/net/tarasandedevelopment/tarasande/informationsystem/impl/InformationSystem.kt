package net.tarasandedevelopment.tarasande.informationsystem.impl

import com.mojang.blaze3d.platform.GlDebugInfo
import net.minecraft.util.Util
import net.tarasandedevelopment.tarasande.informationsystem.Information
import net.tarasandedevelopment.tarasande.util.platform.DBus
import net.tarasandedevelopment.tarasande.util.platform.Spotify

class InformationCPU : Information("System", "CPU") {

    override fun getMessage() = GlDebugInfo.getCpuInfo()!!
}

class InformationGPU : Information("System", "GPU") {

    override fun getMessage() = GlDebugInfo.getRenderer()!!
}

class InformationPortage : Information("System", "Portage") {

    companion object {
        fun isGenlopInstalled(): Boolean {
            if (Util.getOperatingSystem() != Util.OperatingSystem.LINUX)
                return false

            return try {
                Runtime.getRuntime().exec("genlop")
                true
            } catch (t: Throwable) {
                false
            }
        }
    }

    private var lastState = ""

    init {
        Thread({
            while (true) {
                Thread.sleep(100L)

                lastState = try {
                    String(Runtime.getRuntime().exec("genlop -c -n").inputStream.readAllBytes())
                } catch (t: Throwable) {
                    t.toString()
                }
            }
        }, "Genlop query thread").start()
    }

    override fun getMessage(): String? {
        if (lastState.contains("no working merge found.") || lastState.isEmpty())
            return null
        return "\n" + lastState
    }
}

class InformationNowPlaying : Information("System", "Now playing") {

    private var lastState: String? = null

    init {
        Thread({
            while (true) {
                Thread.sleep(1000L)

                lastState = try {
                    when (Util.getOperatingSystem()) {
                        Util.OperatingSystem.WINDOWS -> Spotify.getTrack()
                        Util.OperatingSystem.LINUX -> DBus.setTrack()
                        else -> null // Uninstall whatever you are using at this point
                    }
                } catch (t: Throwable) {
                    null
                }
            }
        }, "Now playing query thread").start()
    }

    override fun getMessage() = lastState

}
