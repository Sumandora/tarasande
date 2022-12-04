package net.tarasandedevelopment.tarasande_windows_platform

import net.fabricmc.api.ClientModInitializer
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventShutdown
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.ScreenExtensionSidebarMultiplayerScreen
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.EntrySidebarPanel
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.EntrySidebarPanelToggleable
import net.tarasandedevelopment.tarasande.util.connection.Proxy
import net.tarasandedevelopment.tarasande.util.connection.ProxyType
import net.tarasandedevelopment.tarasande_windows_platform.information.InformationWindowsSpotify
import org.spongepowered.include.com.google.common.io.Files
import su.mandora.event.EventDispatcher
import java.io.File
import java.net.InetSocketAddress

const val NETWORK = "Network"
class TarasandeWindowsPlatform : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {

            TarasandeMain.managerScreenExtension().get(ScreenExtensionSidebarMultiplayerScreen::class.java).sidebar.apply {
                add(object : EntrySidebarPanel("Fritz!Box Reconnect", NETWORK) {

                    private val scriptName = "ip_changer_fritzbox.vbs"
                    private val script = File(TarasandeMain.get().rootDirectory, scriptName)

                    init {
                        if (!script.exists()) {
                            Files.write(TarasandeWindowsPlatform::class.java.getResourceAsStream(scriptName)?.readAllBytes() ?: error("$scriptName not found"), script)
                        }
                    }

                    override fun onClick(mouseButton: Int) {
                        val builder = ProcessBuilder("wscript", this.script.absolutePath)

                        builder.directory(TarasandeMain.get().rootDirectory)
                        builder.start()
                    }
                })

                add(object : EntrySidebarPanelToggleable(this, "Tor Network", NETWORK) {

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

                        val screenBetterProxy = TarasandeMain.managerScreenExtension().get(ScreenExtensionSidebarMultiplayerScreen::class.java).screenBetterSlotListAccountManager.screenBetterProxy

                        if (torProcess == null) {
                            torProcess = ProcessBuilder(torFile.absolutePath).start()
                            screenBetterProxy.proxy = Proxy(InetSocketAddress("127.0.0.1", 9050), ProxyType.SOCKS5)
                        } else {
                            torProcess!!.destroy()
                            torProcess = null
                            screenBetterProxy.proxy = null
                        }
                    }
                })
            }

            TarasandeMain.managerInformation().add(
                InformationWindowsSpotify()
            )
        }
    }
}
