package net.tarasandedevelopment.tarasande_linux_platform.information

import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information

class InformationPortage : Information("Linux", "Portage") {

    companion object {
        fun isGenlopInstalled(): Boolean {
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

class InformationNowPlaying : Information("Linux", "Now playing") {

    private var lastState: String? = null

    private fun askDBus(): String? {
        val trackInfo = String(ProcessBuilder("bash",
            "-c",
            "dbus-send " +
                    "--print-reply " +
                    "--dest=" +
                    "$(dbus-send " +
                    "--session " +
                    "--dest=org.freedesktop.DBus " +
                    "--type=method_call " +
                    "--print-reply /org/freedesktop/DBus org.freedesktop.DBus.ListNames " +
                    "| grep org.mpris.MediaPlayer2 " +
                    "| sed -e 's/.*\\\"\\(.*\\)\\\"/\\1/' " +
                    "| head -n 1 " +
                    ") " +
                    "/org/mpris/MediaPlayer2 " +
                    "org.freedesktop.DBus.Properties.Get " +
                    "string:'org.mpris.MediaPlayer2.Player' " +
                    "string:'Metadata'"
        ).start().inputStream.readAllBytes())

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
        Thread({
            while (true) {
                Thread.sleep(1000L)

                lastState = try {
                    askDBus()?.trim()
                } catch (t: Throwable) {
                    null
                }
            }
        }, "Now playing query thread").start()
    }

    override fun getMessage() = lastState

}