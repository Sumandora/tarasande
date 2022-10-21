package net.tarasandedevelopment.tarasande.screen.cheatmenu.information

import com.mojang.blaze3d.platform.GlDebugInfo
import net.minecraft.util.Util
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.information.Information

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

    companion object {
        fun isDBusInstalled(): Boolean {
            if (Util.getOperatingSystem() != Util.OperatingSystem.LINUX)
                return false

            return try {
                Runtime.getRuntime().exec("dbus-send")
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
                    String(ProcessBuilder(
                        "bash",
                        "-c",
                        "dbus-send --print-reply --dest=\$(dbus-send --session --dest=org.freedesktop.DBus --type=method_call --print-reply /org/freedesktop/DBus org.freedesktop.DBus.ListNames | grep org.mpris.MediaPlayer2 | sed -e 's/.*\\\"\\(.*\\)\\\"/\\1/' | head -n 1) /org/mpris/MediaPlayer2 org.freedesktop.DBus.Properties.Get string:'org.mpris.MediaPlayer2.Player' string:'Metadata'"
                    ).start().inputStream.readAllBytes())
                } catch (t: Throwable) {
                    t.toString()
                }
            }
        }, "Now playing query thread").start()
    }

    override fun getMessage(): String? {
        val lines = lastState.split("\n")
        val titleLine = lines.indexOfFirst { it.contains("string \"xesam:title\"") }
        return if (titleLine == -1)
            null
        else
            lines[lines.indexOfFirst { it.contains("string \"xesam:title\"") } + 1].split("string \"")[1].let { it.substring(0, it.length - 1) } // this calculation is the most retarded shit I've ever wrote bruh
    }
}
