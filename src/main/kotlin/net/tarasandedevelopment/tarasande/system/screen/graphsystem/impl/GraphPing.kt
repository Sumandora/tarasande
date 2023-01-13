package net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl

import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.Graph
import su.mandora.event.EventDispatcher

class GraphPing : Graph("Connection", "Ping", 25, true) {

    init {
        EventDispatcher.apply {
            add(EventPacket::class.java) { event ->
                if (event.type == EventPacket.Type.RECEIVE && event.packet is PlayerListS2CPacket)
                    if (event.packet.actions.contains(PlayerListS2CPacket.Action.ADD_PLAYER) || event.packet.actions.contains(PlayerListS2CPacket.Action.UPDATE_LATENCY))
                        event.packet.entries.firstOrNull { it.profile.id == mc.player?.uuid }?.also {
                            add(it.latency)
                        }
            }
            add(EventDisconnect::class.java) {
                if (it.connection == mc.networkHandler?.connection) {
                    clear()
                }
            }
        }
    }
}