package de.florianmichael.tarasande_protocol_spoofer

import de.florianmichael.tarasande_protocol_spoofer.spoofer.*
import net.fabricmc.api.ClientModInitializer
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.ScreenExtensionSidebarMultiplayerScreen
import su.mandora.event.EventDispatcher

class TarasandeProtocolSpoofer : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            TarasandeMain.managerScreenExtension().get(ScreenExtensionSidebarMultiplayerScreen::class.java).sidebar.apply {
                add(
                    EntrySidebarPanelToggleableBungeeHack(this),
                    EntrySidebarPanelToggleableForgeFaker(this),
                    EntrySidebarPanelToggleableHAProxyHack(this),
                    EntrySidebarPanelToggleableQuiltFaker(this),
                    EntrySidebarPanelToggleableVivecraftFaker(this)
                )
            }
        }
    }
}
