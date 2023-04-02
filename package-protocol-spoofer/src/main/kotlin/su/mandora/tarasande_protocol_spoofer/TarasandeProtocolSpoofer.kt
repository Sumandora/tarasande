package su.mandora.tarasande_protocol_spoofer

import su.mandora.tarasande_protocol_spoofer.command.CommandOpenModsRCE
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.ProtocolSpooferValues
import su.mandora.tarasande_protocol_spoofer.viaversion.ViaVersionUtil
import io.netty.buffer.Unpooled
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.network.ClientConnection
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket
import net.minecraft.util.Identifier
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventConnectServer
import su.mandora.tarasande.event.impl.EventSuccessfulLoad
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues
import su.mandora.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import su.mandora.tarasande.system.feature.commandsystem.ManagerCommand

class TarasandeProtocolSpoofer : ClientModInitializer {

    companion object {
        val viaFabricPlusLoaded = FabricLoader.getInstance().isModLoaded("viafabricplus")
        var clientConnection: ClientConnection? = null

        fun enforcePluginMessage(channel: String, oldChannel: String? = null, value: ByteArray) {
            if (viaFabricPlusLoaded && oldChannel != null && ViaVersionUtil.sendLegacyPluginMessage(oldChannel, value)) {
                return
            }

            MinecraftClient.getInstance().networkHandler!!.sendPacket(CustomPayloadC2SPacket(Identifier(channel), PacketByteBuf(Unpooled.buffer()).writeByteArray(value)))
        }
    }

    override fun onInitializeClient() {
        EventDispatcher.apply {
            add(EventSuccessfulLoad::class.java) {
                if (viaFabricPlusLoaded) {
                    ViaVersionUtil.builtForgeChannelMappings()
                    ManagerCommand.add(CommandOpenModsRCE())
                }
                ValueButtonOwnerValues(TarasandeValues, "Protocol spoofer values", ProtocolSpooferValues)
            }
            add(EventConnectServer::class.java) {
                clientConnection = it.connection
            }
        }
    }
}
