package net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.Graph
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.GraphTickable
import su.mandora.event.EventDispatcher
import java.util.*

class GraphOnlinePlayers : GraphTickable("Server", "Online Players", 25, true) {

    private var oldPlayers = 0

    override fun tick(): Number? {
        val size = MinecraftClient.getInstance().networkHandler?.listedPlayerListEntries?.size ?: return null
        if (oldPlayers != size) {
            oldPlayers = size
            return size
        }
        return null
    }
}
