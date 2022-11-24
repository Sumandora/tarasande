package net.tarasandedevelopment.tarasande.systems.screen.graphsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.Graph
import su.mandora.event.EventDispatcher

class GraphPing : Graph("Ping", 10, true) {

    init {
        EventDispatcher.apply {
            add(EventPacket::class.java) { event ->
                if (event.type == EventPacket.Type.RECEIVE && event.packet is PlayerListS2CPacket)
                    if (event.packet.action == PlayerListS2CPacket.Action.ADD_PLAYER || event.packet.action == PlayerListS2CPacket.Action.UPDATE_LATENCY)
                        event.packet.entries.firstOrNull { it.profile.id == MinecraftClient.getInstance().player?.uuid }?.also {
                            add(it.latency)
                        }
            }
            add(EventDisconnect::class.java) {
                if (it.connection == MinecraftClient.getInstance().networkHandler?.connection) {
                    clear()
                }
            }
        }
    }
}