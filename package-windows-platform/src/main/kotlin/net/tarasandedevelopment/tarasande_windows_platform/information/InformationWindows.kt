package net.tarasandedevelopment.tarasande_windows_platform.information

import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information
import net.tarasandedevelopment.tarasande.util.extension.javaruntime.Thread

class InformationWindowsSpotify : Information("Windows", "Spotify") {

    private var lastState: String? = null

    init {
        Thread("Now playing query thread") {
            while (true) {
                Thread.sleep(1000L)

                lastState = try {
                    Spotify.getTrack()
                } catch (t: Throwable) {
                    null
                }
            }
        }.start()
    }

    override fun getMessage() = lastState
}
