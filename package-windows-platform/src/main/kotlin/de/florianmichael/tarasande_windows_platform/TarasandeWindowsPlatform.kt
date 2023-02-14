package de.florianmichael.tarasande_windows_platform

import de.florianmichael.tarasande_windows_platform.information.InformationWindowsSpotify
import net.fabricmc.api.ClientModInitializer
import net.minecraft.util.Util
import net.tarasandedevelopment.tarasande.TARASANDE_NAME
import net.tarasandedevelopment.tarasande.event.EventShutdown
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.system.base.filesystem.ManagerFile
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.ManagerInformation
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.ScreenExtensionSidebarMultiplayerScreen
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.sidebar.SidebarEntryProxy
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.SidebarEntry
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.SidebarEntryToggleable
import net.tarasandedevelopment.tarasande.util.connection.Proxy
import net.tarasandedevelopment.tarasande.util.connection.ProxyType
import org.spongepowered.include.com.google.common.io.Files
import su.mandora.event.EventDispatcher
import java.io.File
import java.net.InetSocketAddress
import java.util.logging.Logger

const val NETWORK = "Network"
class TarasandeWindowsPlatform : ClientModInitializer {
    private val logger = Logger.getLogger("$TARASANDE_NAME-windows-platform")!!

    override fun onInitializeClient() {
        val operatingSystem = Util.getOperatingSystem()
        if(operatingSystem != Util.OperatingSystem.WINDOWS) {
            logger.warning("$TARASANDE_NAME Windows Platform is not designed to run on '" + operatingSystem.getName() + "' systems")
            return
        }
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            ManagerScreenExtension.get(ScreenExtensionSidebarMultiplayerScreen::class.java).sidebar.apply {
                add(
                    object : SidebarEntry("Fritz!Box Reconnect", NETWORK) {

                        private val scriptName = "ip_changer_fritzbox.vbs"
                        private val script = File(ManagerFile.rootDirectory, scriptName)

                        init {
                            if (!script.exists()) {
                                Files.write(TarasandeWindowsPlatform::class.java.getResourceAsStream(scriptName)?.readAllBytes() ?: error("$scriptName not found"), script)
                            }
                        }

                        override fun onClick(mouseButton: Int) {
                            val builder = ProcessBuilder("wscript", this.script.absolutePath)

                            builder.directory(ManagerFile.rootDirectory)
                            builder.start()
                        }
                    },
                    object : SidebarEntryToggleable("Tor Network", NETWORK) {

                        private val torFile = File(System.getProperty("user.home") + "/Desktop", "Tor Browser/Browser/TorBrowser/Tor/tor.exe")
                        private var torProcess: Process? = null

                        init {
                            EventDispatcher.add(EventShutdown::class.java) {
                                torProcess?.destroy()
                                torProcess = null
                            }
                        }

                        override fun onClick(mouseButton: Int) {
                            val screenBetterProxy = ManagerScreenExtension.get(ScreenExtensionSidebarMultiplayerScreen::class.java).sidebar.get(SidebarEntryProxy::class.java).screenBetterProxy

                            if (enabled.value) {
                                torProcess = ProcessBuilder(torFile.absolutePath).start()
                                screenBetterProxy.proxy = Proxy(InetSocketAddress("127.0.0.1", 9050), ProxyType.SOCKS5)
                            } else {
                                torProcess!!.destroy()
                                torProcess = null
                                screenBetterProxy.proxy = null
                            }
                        }
                    }
                )
            }

            ManagerInformation.add(
                InformationWindowsSpotify()
            )
        }
    }
}
