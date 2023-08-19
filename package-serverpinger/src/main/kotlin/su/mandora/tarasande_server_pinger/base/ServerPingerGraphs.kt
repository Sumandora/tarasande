package su.mandora.tarasande_server_pinger.base

import net.minecraft.client.network.ServerInfo
import net.minecraft.util.Formatting
import su.mandora.tarasande.system.screen.graphsystem.Graph

object GraphPlayers : Graph("", "Players", 10, true)
object GraphPing : Graph("", "Ping", 10, true)

fun update(server: ServerInfo) {
    Formatting.strip(server.playerCountLabel?.string)?.apply {
        if (this.contains("/")) GraphPlayers.add(this.split("/")[0].toInt())
    }

    if (server.ping != 0L) {
        GraphPing.add(server.ping)
    }
}
