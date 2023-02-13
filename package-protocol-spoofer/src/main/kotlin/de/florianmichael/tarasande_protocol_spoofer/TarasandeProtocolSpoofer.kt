package de.florianmichael.tarasande_protocol_spoofer

import de.florianmichael.tarasande_protocol_spoofer.command.CommandOpenModsRCE
import de.florianmichael.tarasande_protocol_spoofer.spoofer.*
import de.florianmichael.tarasande_protocol_spoofer.viaversion.ViaVersionUtil
import io.netty.buffer.Unpooled
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket
import net.minecraft.util.Identifier
import net.tarasandedevelopment.tarasande.TARASANDE_NAME
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.system.feature.commandsystem.ManagerCommand
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.ScreenExtensionSidebarMultiplayerScreen
import su.mandora.event.EventDispatcher

class TarasandeProtocolSpoofer : ClientModInitializer {

    companion object {
        val tarasandeProtocolHackLoaded = FabricLoader.getInstance().isModLoaded("$TARASANDE_NAME-protocol-hack")

        fun enforcePluginMessage(channel: String, oldChannel: String? = null, value: ByteArray) {
            if (tarasandeProtocolHackLoaded && ViaVersionUtil.sendLegacyPluginMessage(oldChannel!!, value)) {
                return
            }

            MinecraftClient.getInstance().networkHandler!!.sendPacket(CustomPayloadC2SPacket(Identifier(channel), PacketByteBuf(Unpooled.buffer()).writeByteArray(value)))
        }
    }

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            val sidebar = ManagerScreenExtension.get(ScreenExtensionSidebarMultiplayerScreen::class.java).sidebar

            if (tarasandeProtocolHackLoaded) {
                ViaVersionUtil.builtForgeChannelMappings()

                sidebar.add(
                    SidebarEntryToggleableForgeFaker(),
                    SidebarEntryToggleableTeslaClientFaker()
                )

                ManagerCommand.add(
                    CommandOpenModsRCE()
                )
            }

            sidebar.add(
                SidebarEntryToggleableBungeeHack(),
                SidebarEntryToggleableHAProxyHack(),
                SidebarEntryToggleableQuiltFaker(),
                SidebarEntryToggleableVivecraftFaker(),
                SidebarEntryToggleablePluginMessageFilter()
            )
        }
    }
}
