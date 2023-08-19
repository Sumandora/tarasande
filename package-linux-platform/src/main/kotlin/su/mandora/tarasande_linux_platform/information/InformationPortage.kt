package su.mandora.tarasande_linux_platform.information

import su.mandora.tarasande.system.screen.informationsystem.Information
import su.mandora.tarasande.util.extension.javaruntime.Thread

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
        Thread("Genlop query thread") {
            while (true) {
                Thread.sleep(100L)

                lastState = try {
                    String(Runtime.getRuntime().exec("genlop -c -n").inputStream.readAllBytes())
                } catch (t: Throwable) {
                    t.toString()
                }
            }
        }.start()
    }

    override fun getMessage(): String? {
        if (lastState.contains("no working merge found.") || lastState.isEmpty())
            return null
        return "\n" + lastState
    }
}
