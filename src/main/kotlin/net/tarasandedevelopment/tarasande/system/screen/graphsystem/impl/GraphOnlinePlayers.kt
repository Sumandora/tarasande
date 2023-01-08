package net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl

import net.tarasandedevelopment.tarasande.system.screen.graphsystem.GraphTickable
import net.tarasandedevelopment.tarasande.util.extension.mc

class GraphOnlinePlayers : GraphTickable("Server", "Online Players", 25, true) {

    private var oldPlayers = 0

    override fun tick(): Number? {
        val size = mc.networkHandler?.listedPlayerListEntries?.size ?: return null
        if (oldPlayers != size) {
            oldPlayers = size
            return size
        }
        return null
    }
}
