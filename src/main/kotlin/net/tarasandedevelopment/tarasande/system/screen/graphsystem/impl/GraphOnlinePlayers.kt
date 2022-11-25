package net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.Graph
import su.mandora.event.EventDispatcher
import java.util.*

class GraphOnlinePlayers : Graph("Online Players", 10, true) {

    val players = ArrayList<UUID>()

    init {
        EventDispatcher.apply {
            add(EventPacket::class.java) { event ->
                if(event.type == EventPacket.Type.RECEIVE && event.packet is PlayerListS2CPacket) {
                    val uuids = event.packet.entries.mapNotNull { it.profile.id }
                    if(uuids.isEmpty())
                        return@add
                    val prevSize = players.size
                    when(event.packet.action) {
                        PlayerListS2CPacket.Action.ADD_PLAYER -> players.addAll(uuids.filter { !players.contains(it) })
                        PlayerListS2CPacket.Action.REMOVE_PLAYER -> players.removeAll(uuids.toSet())
                        else -> return@add
                    }
                    if(players.size != prevSize)
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