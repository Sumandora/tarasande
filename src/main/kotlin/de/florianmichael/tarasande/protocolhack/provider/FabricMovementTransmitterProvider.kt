package de.florianmichael.tarasande.protocolhack.provider

import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider

// My Protocol Hack removes all usages of this provider, it's just a ViaVersion Convention to add it
class FabricMovementTransmitterProvider : MovementTransmitterProvider() {

    override fun getFlyingPacket(): Any? {
        return null
    }

    override fun getGroundPacket(): Any? {
        return null
    }

    override fun sendPlayer(userConnection: UserConnection?) {
    }
}