package de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.handler

import io.netty.buffer.Unpooled
import net.minecraft.network.ClientConnection
import net.minecraft.network.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.IForgeNetClientHandler
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.payload.legacy.ModStruct

enum class ModernFmlState {
    FML_2,
    FML_3,
    FML_4 // not really fml v4, just some inner changes
}

class ModernFmlNetClientHandler(val state: ModernFmlState, val connection: ClientConnection) : IForgeNetClientHandler {

    private val modTracker = ArrayList<ModStruct>()

    override fun onIncomingPacket(packet: Packet<*>): Boolean {
        if (packet is LoginQueryRequestS2CPacket) {
            val channel = packet.channel.toString()

            if (channel == "fml:loginwrapper") {
                val buffer = packet.payload
                val subChannel = buffer.readString()

                if (subChannel.equals("fml:handshake")) {
                    buffer.readVarInt()

                    when (buffer.readVarInt()) { // packet Id
                        0x01 -> this.onServerModList(packet.queryId, buffer)
                        0x03, 0x04 -> this.sendAck(packet.queryId)
                    }
                    return true
                }
            }
        }
        return false
    }

    private fun onServerModList(queryId: Int, buffer: PacketByteBuf) {
        val mods = arrayOfNulls<String>(buffer.readVarInt())

        for (i in 0..mods.size) {
            mods[i] = buffer.readString()
            modTracker.add(ModStruct(mods[i]!!, "not implemented in FML 2"))
        }

        val channels = arrayOfNulls<String>(buffer.readVarInt())
        val channelMarkers = arrayOfNulls<String>(channels.size)

        for (i in channels.indices) {
            channels[i] = buffer.readString()
            channelMarkers[i] = buffer.readString()
        }

        val registries = arrayOfNulls<String>(buffer.readVarInt())

        for (i in registries.indices) {
            registries[i] = buffer.readString()
        }

        var dataPacks: Array<RegistryKey<out Registry<*>>?>? = null
        if (state != ModernFmlState.FML_2 && buffer.isReadable) {
            dataPacks = arrayOfNulls(buffer.readVarInt())

            for (i in dataPacks.indices) {
                dataPacks[i] = RegistryKey.ofRegistry<Any?>(buffer.readIdentifier())
            }
        }

        this.sendModList(queryId, mods, channels, channelMarkers, registries, dataPacks)
    }

    private fun sendModList(queryId: Int, mods: Array<String?>, channels: Array<String?>, channelMarkers: Array<String?>, registries: Array<String?>, dataPacks: Array<RegistryKey<out Registry<*>>?>?) {
        val buffer = PacketByteBuf(Unpooled.buffer())

        buffer.writeVarInt(0x02)
        buffer.writeVarInt(mods.size)

        mods.forEach {
            buffer.writeString(it)
        }

        buffer.writeVarInt(channels.size)

        channels.forEachIndexed { index, s ->
            buffer.writeString(s)
            buffer.writeString(channelMarkers[index])
        }

        buffer.writeVarInt(registries.size)

        registries.forEach {
            buffer.writeString(it)
            buffer.writeString("")
        }

        if (state != ModernFmlState.FML_2) {
            if (dataPacks != null || state == ModernFmlState.FML_4) {
                dataPacks!!.forEach {
                    it?.also {
                        buffer.writeIdentifier(it.registry)
                    }
                }
            }
        }

        this.sendWrappedPacket(queryId, buffer)
    }

    private fun sendAck(queryId: Int) {
        val buffer = PacketByteBuf(Unpooled.buffer())

        buffer.writeVarInt(0x63)

        this.sendWrappedPacket(queryId, buffer)
    }

    private fun sendWrappedPacket(queryId: Int, buffer: PacketByteBuf) {
        val out = PacketByteBuf(Unpooled.buffer())

        out.writeString("fml:handshake")
        out.writeVarInt(buffer.readableBytes())
        out.writeBytes(buffer)
        buffer.release()

        connection.send(LoginQueryResponseC2SPacket(queryId, out))
    }

    override fun handshakeMark(): String {
        if (state != ModernFmlState.FML_2) {
            return "\u0000FML3\u0000"
        }
        return "\u0000FML2\u0000"
    }
}