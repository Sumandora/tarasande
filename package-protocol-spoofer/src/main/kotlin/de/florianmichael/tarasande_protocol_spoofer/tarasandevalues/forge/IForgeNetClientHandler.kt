package de.florianmichael.tarasande_protocol_spoofer.tarasandevalues.forge

import net.minecraft.network.Packet

interface IForgeNetClientHandler {

    fun onIncomingPacket(packet: Packet<*>): Boolean
    fun handshakeMark(): String
}
