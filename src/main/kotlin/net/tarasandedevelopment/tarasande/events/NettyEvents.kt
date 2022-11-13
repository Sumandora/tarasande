package net.tarasandedevelopment.tarasande.events

import io.netty.buffer.ByteBuf
import net.minecraft.network.Packet
import su.mandora.event.Event
import java.net.InetSocketAddress

class EventPacket(val type: Type, val packet: Packet<*>?) : Event(true) {
    enum class Type {
        SEND, RECEIVE
    }
}

class EventPacketTransform(val type: Type, val buf: ByteBuf?) : Event(false) {
    enum class Type {
        DECODE, ENCODE
    }
}

class EventConnectServer(val address: InetSocketAddress) : Event(false)
class EventDisconnect : Event(false)