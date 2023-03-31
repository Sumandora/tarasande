package de.florianmichael.tarasande_protocol_spoofer.tarasandevalues.forge

import net.minecraft.network.packet.Packet

interface IForgeNetClientHandler {

    fun onIncomingPacket(packet: Packet<*>): Boolean
    fun handshakeMark(): String
}
