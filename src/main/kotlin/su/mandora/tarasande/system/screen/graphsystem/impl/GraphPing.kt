package su.mandora.tarasande.system.screen.graphsystem.impl

import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventDisconnect
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.graphsystem.Graph

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