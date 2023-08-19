package su.mandora.tarasande_linux_platform.information

import su.mandora.tarasande.system.screen.informationsystem.Information
import su.mandora.tarasande.util.extension.javaruntime.Thread

class InformationNowPlaying : Information("Linux", "Now playing") {

    private var lastState: String? = null

    private fun askDBus(): String? {
        val player = ProcessBuilder(
            "dbus-send",
            "--session",
            "--dest=org.freedesktop.DBus",
            "--type=method_call",
            "--print-reply",
            "/org/freedesktop/DBus",
            "org.freedesktop.DBus.ListNames"
        ).start()
            .inputStream
            .readAllBytes()
            .decodeToString()
            .split("\n")
            .filter { it.contains("org.mpris.MediaPlayer2") }
            .firstNotNullOfOrNull {
                val pattern = Regex("\"(.*?)\"").toPattern()
                val matcher = pattern.matcher(it)
                if (matcher.find())
                    matcher.group(1)
                else
                    null
            }
            ?: return null
        val trackInfo = ProcessBuilder(
            "dbus-send",
            "--session",
            "--dest=$player",
            "--print-reply",
            "/org/mpris/MediaPlayer2",
            "org.freedesktop.DBus.Properties.Get",
            "string:org.mpris.MediaPlayer2.Player",
            "string:Metadata"
        ).start()
            .inputStream
            .readAllBytes()
            .decodeToString()

        var nextLine = false
        for (line in trackInfo.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            if (nextLine) {
                val secondPart = line.split("string \"".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                return secondPart.substring(0, secondPart.length - 1)
            }
            if (line.contains("string \"xesam:title\"")) {
                nextLine = true
            }
        }
        return null
    }

    init {
        Thread("Now playing query thread") {
            while (true) {
                Thread.sleep(1000L)

                lastState = try {
                    askDBus()?.trim()
                } catch (t: Throwable) {
                    null
                }
            }
        }.start()
    }

    override fun getMessage() = lastState

}
