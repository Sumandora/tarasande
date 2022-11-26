package net.tarasandedevelopment.tarasande_windows_platform.multiplayerfeature

import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventShutdown
import net.tarasandedevelopment.tarasande.system.feature.multiplayerfeaturesystem.MultiplayerFeature
import net.tarasandedevelopment.tarasande.system.feature.multiplayerfeaturesystem.MultiplayerFeatureToggleable
import net.tarasandedevelopment.tarasande.util.connection.Proxy
import net.tarasandedevelopment.tarasande.util.connection.ProxyType
import org.spongepowered.include.com.google.common.io.Files
import su.mandora.event.EventDispatcher
import java.io.File
import java.net.InetSocketAddress

class MultiplayerFeatureWindowsFritzBoxReconnect : MultiplayerFeature("Fritz!Box Reconnect", "Windows") {

    private val scriptName = "ip_changer_fritzbox.vbs"
    private val script = File(TarasandeMain.get().rootDirectory, scriptName)

    init {
        if (!script.exists()) {
            Files.write(TarasandeMain::class.java.getResourceAsStream(scriptName)?.readAllBytes() ?: error("$scriptName not found"), script)
        }
    }

    override fun onClick(mouseButton: Int) {
        val builder = ProcessBuilder("wscript", this.script.absolutePath)

        builder.directory(TarasandeMain.get().rootDirectory)
        builder.start()
    }
}

class MultiplayerFeatureWindowsTorNetwork : MultiplayerFeatureToggleable("Tor Network", "Windows") {

    private val torFile = File(System.getProperty("user.home") + "/Desktop", "Tor Browser/Browser/TorBrowser/Tor/tor.exe")
    private var torProcess: Process? = null

    init {
        EventDispatcher.add(EventShutdown::class.java) {
            torProcess?.destroy()
            torProcess = null
        }
    }

    override fun onClick(state: Boolean) {
        super.onClick(state)

        if (torProcess == null) {
            torProcess = ProcessBuilder(torFile.absolutePath).start()
            TarasandeMain.get().proxy = Proxy(InetSocketAddress("127.0.0.1", 9050), ProxyType.SOCKS5)
        } else {
            torProcess!!.destroy()
            torProcess = null
            TarasandeMain.get().proxy = null
        }
    }
}
