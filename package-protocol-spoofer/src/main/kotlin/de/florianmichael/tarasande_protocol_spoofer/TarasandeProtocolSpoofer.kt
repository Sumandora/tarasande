package de.florianmichael.tarasande_protocol_spoofer

import de.florianmichael.tarasande_protocol_spoofer.spoofer.*
import de.florianmichael.tarasande_protocol_spoofer.viaversion.ViaVersionExtensions
import net.fabricmc.api.ClientModInitializer
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.ScreenExtensionSidebarMultiplayerScreen
import su.mandora.event.EventDispatcher

class TarasandeProtocolSpoofer : ClientModInitializer {

    companion object {
        private var isViaLoaded = false

        fun checkVia() {
            try {
                Class.forName("net.tarasandedevelopment.tarasande_protocol_hack.TarasandeProtocolHack")
                ViaVersionExtensions.builtForgeChannelMappings()
                isViaLoaded = true
            } catch (_: Exception) {
            }
        }

        fun isVia() = isViaLoaded
    }

    override fun onInitializeClient() {
        checkVia()
        if (isVia()) {
            ViaVersionExtensions.builtForgeChannelMappings()
        }

        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            TarasandeMain.managerScreenExtension().get(ScreenExtensionSidebarMultiplayerScreen::class.java).sidebar.apply {
                add(
                    EntrySidebarPanelToggleableBungeeHack(this),
                    EntrySidebarPanelToggleableForgeFaker(this),
                    EntrySidebarPanelToggleableHAProxyHack(this),
                    EntrySidebarPanelToggleableQuiltFaker(this),
                    EntrySidebarPanelToggleableVivecraftFaker(this),
                    EntrySidebarPanelToggleableTeslaClientFaker(this)
                )
            }
        }
    }
}
