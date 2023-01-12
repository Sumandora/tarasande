package de.florianmichael.tarasande_protocol_spoofer

import de.florianmichael.tarasande_protocol_spoofer.spoofer.*
import de.florianmichael.tarasande_protocol_spoofer.viaversion.ViaVersionUtil
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.ScreenExtensionSidebarMultiplayerScreen
import su.mandora.event.EventDispatcher

class TarasandeProtocolSpoofer : ClientModInitializer {

    companion object {
        val tarasandeProtocolHackLoaded = FabricLoader.getInstance().isModLoaded("tarasande-protocol-hack")
    }

    override fun onInitializeClient() {
        if (tarasandeProtocolHackLoaded) {
            ViaVersionUtil.builtForgeChannelMappings()
        }

        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            ManagerScreenExtension.get(ScreenExtensionSidebarMultiplayerScreen::class.java).sidebar.apply {
                add(
                    EntrySidebarPanelToggleableBungeeHack(),
                    EntrySidebarPanelToggleableForgeFaker(),
                    EntrySidebarPanelToggleableHAProxyHack(),
                    EntrySidebarPanelToggleableQuiltFaker(),
                    EntrySidebarPanelToggleableVivecraftFaker(),
                    EntrySidebarPanelToggleablePluginMessageFilter()
                )
            }
        }
    }
}
