package net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.Graph
import su.mandora.event.EventDispatcher
import java.util.*

class GraphOnlinePlayers : Graph("Server", "Online Players", 25, true) {

    val players = ArrayList<UUID>()

    init {
        EventDispatcher.apply {
            add(EventPacket::class.java) { event ->
                if (event.type == EventPacket.Type.RECEIVE) {
                    val prevSize = players.size
                    when (event.packet) {
                        is PlayerListS2CPacket -> {
                            players.addAll(event.packet.playerAdditionEntries.map { it.profileId })
                        }
                        is PlayerRemoveS2CPacket -> {
                            players.removeAll(event.packet.profileIds.toSet())
                        }
                    }
                    if (players.size != prevSize)
                        add(players.size)
                }
            }
            add(EventDisconnect::class.java) {
                if (it.connection == MinecraftClient.getInstance().networkHandler?.connection) {
                    players.clear()
                    clear()
                }
            }
        }
    }
}