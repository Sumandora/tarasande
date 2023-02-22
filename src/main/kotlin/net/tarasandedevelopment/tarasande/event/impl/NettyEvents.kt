package net.tarasandedevelopment.tarasande.event.impl

import io.netty.buffer.ByteBuf
import net.minecraft.network.ClientConnection
import net.minecraft.network.Packet
import net.tarasandedevelopment.tarasande.event.Event
import java.util.*

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

class EventConnectServer(val connection: ClientConnection) : Event(false)
class EventDisconnect(val connection: ClientConnection) : Event(false)
class EventInvalidPlayerInfo(val uuid: UUID) : Event(false)