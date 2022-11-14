package net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.impl.forgefaker

import net.minecraft.network.Packet

interface IForgeNetClientHandler {

    fun onIncomingPacket(packet: Packet<*>): Boolean
    fun handshakeMark(): String
}
