package net.tarasandedevelopment.tarasande_windows_platform.information

import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information

class InformationWindowsSpotify : Information("Windows", "Spotify") {

    private var lastState: String? = null

    init {
        Thread({
            while (true) {
                Thread.sleep(1000L)

                lastState = try {
                    Spotify.track
                } catch (t: Throwable) {
                    null
                }
            }
        }, "Now playing query thread").start()
    }

    override fun getMessage() = lastState
}
