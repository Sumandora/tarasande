package su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.handler

import io.netty.buffer.Unpooled
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.minecraft.client.network.MultiplayerServerListPinger
import net.minecraft.client.network.ServerInfo
import net.minecraft.network.ClientConnection
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import su.mandora.tarasande.TARASANDE_NAME
import su.mandora.tarasande_protocol_spoofer.injection.accessor.IServerInfo
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.ForgeProtocolSpoofer
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.IForgeNetClientHandler
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.payload.legacy.ModStruct
import java.net.InetSocketAddress

class Fml1NetClientHandler(private val connection: ClientConnection) : IForgeNetClientHandler {

    private val itemMappings = Int2ObjectOpenHashMap<String>()
    private val blockMappings = Int2ObjectOpenHashMap<String>()
    private val entityMappings = Int2ObjectOpenHashMap<String>()

    private val forgeHS = "fml:hs"
    private val maxStringLength = 32767

    private fun handlerServerHello(buf: PacketByteBuf) {
        val version = buf.readByte()
        if (version > 1) {
            buf.readInt() // unused dimension
        }

        val newPacket = PacketByteBuf(Unpooled.buffer())
        newPacket.writeString("fml:hs\u0000FML\u0000FML|MP\u0000FORGE")

        this.sendCustomPayload("minecraft:register", newPacket)
        this.sendClientHello(version)

        if (ForgeProtocolSpoofer.useFML1Cache.value) {
            ForgeProtocolSpoofer.forgeInfoTracker[this.connection.address]?.also {
                this.sendModList(it.installedMods())
            }
        } else {
            val address = connection.address as InetSocketAddress
            val serverInfo = ServerInfo(address.hostName + ":" + address.port, address.hostName + ":" + address.port, false)
            MultiplayerServerListPinger().add(serverInfo) {
            }
            @Suppress("KotlinConstantConditions")
            val payload = (serverInfo as IServerInfo).tarasande_getForgePayload()
            if (payload == null) {
                connection.disconnect(Text.of("[$TARASANDE_NAME] Failed to get mods, try to enable FML1 Cache in ForgeFaker"))
                return
            }
            this.sendModList(payload.installedMods())
        }
    }

    private fun handleModList(buf: PacketByteBuf) {
        buf.readInt()
        this.sendHandshakeAck(2)
    }

    private fun handleHandshakeAck(buf: PacketByteBuf) {
        val state = buf.readByte()

        if (state in 2..3) {
            this.sendHandshakeAck((state + 2).toByte())
        }
    }

    private fun handleRegistryData(buf: PacketByteBuf) {
        buf.readBoolean()

        val registryName = buf.readString(maxStringLength)
        var count = buf.readVarInt()

        for (i in 0 until count) {
            val name = buf.readString(maxStringLength)
            val id = buf.readVarInt()

            when (registryName.lowercase()) {
                "minecraft:blocks" -> blockMappings[id] = name
                "minecraft:items" -> itemMappings[id] = name
                "minecraft:entities" -> entityMappings[id] = name
            }
        }
        count = buf.readVarInt()
        for (i in 0 until count) {
            buf.readString(maxStringLength)
        }

        if (buf.readableBytes() > 0) {
            count = buf.readVarInt()

            for (i in 0 until count) {
                buf.readString(maxStringLength)
            }
        }

        sendHandshakeAck(3.toByte())
    }

    private fun sendModList(mods: List<ModStruct>) {
        val buffer = PacketByteBuf(Unpooled.buffer())

        buffer.writeByte(0x02)
        buffer.writeVarInt(mods.size)

        mods.forEach {
            buffer.writeString(it.modId)
            buffer.writeString(it.modVersion)
        }

        this.sendCustomPayload(forgeHS, buffer)
    }

    private fun sendClientHello(fmlProtocolVersion: Byte) {
        val buffer = PacketByteBuf(Unpooled.buffer())

        buffer.writeByte(0x01)
        buffer.writeByte(fmlProtocolVersion.toInt())

        this.sendCustomPayload(forgeHS, buffer)
    }

    private fun sendCustomPayload(channel: String, buf: PacketByteBuf) = connection.send(CustomPayloadC2SPacket(Identifier(channel), buf))

    private fun sendHandshakeAck(state: Byte) {
        val buffer = PacketByteBuf(Unpooled.buffer())

        buffer.writeByte(255)
        buffer.writeByte(state.toInt())

        this.sendCustomPayload(this.forgeHS, buffer)
    }

    override fun onIncomingPacket(packet: Packet<*>): Boolean {
        if (packet is CustomPayloadS2CPacket) {
            val channel = packet.channel.toString()
            val data = packet.data

            data.readerIndex(0)

            if (channel == forgeHS) {
                val packetId = data.readUnsignedByte()

                when (packetId.toInt()) {
                    0x00 -> this.handlerServerHello(data)
                    0x02 -> this.handleModList(data)
                    0x03 -> this.handleRegistryData(data)
                    0xFF -> this.handleHandshakeAck(data)
                }
                return true
            }
            packet.data.readerIndex(0)
            return false
        }
        return false
    }

    override fun handshakeMark() = "\u0000FML\u0000"
}