package de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker

import net.minecraft.network.Packet

interface IForgeNetClientHandler {

    fun onIncomingPacket(packet: Packet<*>): Boolean
    fun handshakeMark(): String
}
