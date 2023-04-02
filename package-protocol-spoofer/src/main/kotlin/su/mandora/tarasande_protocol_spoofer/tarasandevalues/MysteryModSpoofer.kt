package su.mandora.tarasande_protocol_spoofer.tarasandevalues

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.handler.codec.DecoderException
import net.minecraft.network.ClientConnection
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket
import net.minecraft.util.Identifier
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventConnectServer
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.injection.accessor.IClientConnection
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.util.player.chat.CustomChat

object MysteryModSpoofer {
    private val enabled = ValueBoolean(this, "Enabled", false)
    private val debug = ValueBoolean(this, "Debug", false)

    private var clientConnection: ClientConnection? = null

    init {
        EventDispatcher.apply {
            add(EventConnectServer::class.java) {
                clientConnection = it.connection
            }
            add(EventPacket::class.java) {
                if (!enabled.value) return@add

                if (it.type == EventPacket.Type.RECEIVE && it.packet is CustomPayloadS2CPacket) {
                    val packet = it.packet as CustomPayloadS2CPacket

                    if (packet.channel.toString() == "mysterymod:mm") {
                        it.cancelled = true
                        val buf = packet.data.copy()

                        if (buf.readableBytes() <= 0) return@add
                        val messageKey = readStringFromBuffer(32767, buf)

                        if (buf.readableBytes() <= 0) return@add
                        val message = readStringFromBuffer(32767, buf)

                        if (debug.value) {
                            CustomChat.printChatMessage("[MysteryMod] `$messageKey: $message`")
                        }

                        if (messageKey!! == "mysterymod_user_check") {
                            val buffer = PacketByteBuf(Unpooled.buffer())
                            buffer.writeString(message)

                            (clientConnection as IClientConnection).tarasande_forceSend(CustomPayloadC2SPacket(
                                Identifier("mysterymod", "mm"),
                                buffer
                            ))
                        }
                    }
                }
            }
        }
    }

    private fun readStringFromBuffer(maxLength: Int, packetBuffer: ByteBuf): String? {
        val i = readVarIntFromBuffer(packetBuffer)
        return if (i > maxLength * 4) {
            throw DecoderException("The received encoded string buffer length is longer than maximum allowed (" + i + " > " + maxLength * 4 + ")")
        } else if (i < 0) {
            throw DecoderException("The received encoded string buffer length is less than zero! Weird string!")
        } else {
            val byteBuf: ByteBuf = packetBuffer.readBytes(i)
            val bytes: ByteArray
            if (byteBuf.hasArray()) {
                bytes = byteBuf.array()
            } else {
                bytes = ByteArray(byteBuf.readableBytes())
                byteBuf.getBytes(byteBuf.readerIndex(), bytes)
            }
            val s = String(bytes, Charsets.UTF_8)
            if (s.length > maxLength) {
                throw DecoderException("The received string length is longer than maximum allowed ($i > $maxLength)")
            } else {
                s
            }
        }
    }

    private fun readVarIntFromBuffer(packetBuffer: ByteBuf): Int {
        var i = 0
        var j = 0
        var b0: Byte
        do {
            b0 = packetBuffer.readByte()
            i = i or (b0.toInt() and 127 shl j++ * 7)
            if (j > 5) {
                throw RuntimeException("VarInt too big")
            }
        } while (b0.toInt() and 128 == 128)
        return i
    }
}
