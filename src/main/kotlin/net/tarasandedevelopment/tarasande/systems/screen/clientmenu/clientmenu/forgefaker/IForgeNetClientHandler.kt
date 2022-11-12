package net.tarasandedevelopment.tarasande.systems.screen.clientmenu.clientmenu.forgefaker

import net.minecraft.network.Packet

interface IForgeNetClientHandler {

    fun onIncomingPacket(packet: Packet<*>): Boolean
    fun handshakeMark(): String
}
