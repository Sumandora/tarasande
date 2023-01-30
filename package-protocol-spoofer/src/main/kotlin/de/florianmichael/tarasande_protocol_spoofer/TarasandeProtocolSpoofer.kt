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
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.system.feature.commandsystem.ManagerCommand
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.ScreenExtensionSidebarMultiplayerScreen
import su.mandora.event.EventDispatcher

class TarasandeProtocolSpoofer : ClientModInitializer {

    companion object {
        val tarasandeProtocolHackLoaded = FabricLoader.getInstance().isModLoaded("tarasande-protocol-hack")

        fun enforcePluginMessage(channel: String, value: ByteArray, remap: Boolean = false) {
            val modernPayload = CustomPayloadC2SPacket(Identifier(if (remap) channel.lowercase().replace("|", ":") else channel), PacketByteBuf(Unpooled.buffer()).writeByteArray(value))
            if (tarasandeProtocolHackLoaded) {
                if (!ViaVersionUtil.sendLegacyPluginMessage(channel, value)) MinecraftClient.getInstance().networkHandler!!.sendPacket(modernPayload)
            } else {
                MinecraftClient.getInstance().networkHandler!!.sendPacket(modernPayload)
            }
        }
    }

    override fun onInitializeClient() {
        if (tarasandeProtocolHackLoaded) {
            ViaVersionUtil.builtForgeChannelMappings()
        }

        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            ManagerScreenExtension.get(ScreenExtensionSidebarMultiplayerScreen::class.java).sidebar.apply {
                add(
                    SidebarEntryToggleableBungeeHack(),
                    SidebarEntryToggleableForgeFaker(),
                    SidebarEntryToggleableHAProxyHack(),
                    SidebarEntryToggleableQuiltFaker(),
                    SidebarEntryToggleableVivecraftFaker(),
                    SidebarEntryToggleableTeslaClientFaker(),
                    SidebarEntryToggleablePluginMessageFilter()
                )
            }

            ManagerCommand.add(
                CommandOpenModsRCE()
            )
        }
    }
}
