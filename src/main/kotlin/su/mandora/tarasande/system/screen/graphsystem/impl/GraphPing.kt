package su.mandora.tarasande.system.screen.graphsystem.impl

import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket
import net.minecraft.util.Util
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventDisconnect
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.screen.graphsystem.Graph
import su.mandora.tarasande.system.screen.graphsystem.panel.PanelGraph
import su.mandora.tarasande.system.screen.panelsystem.ManagerPanel

class GraphPing : Graph("Connection", "Ping", 25, true) {

    private val calculationMethod = object : ValueMode(this, "Calculation method", false, "Player list", "Debug hud") {
        override fun onChange(index: Int, oldSelected: Boolean, newSelected: Boolean) {
            clear()
        }
    }
    private val spoofOpenedDebugHud = ValueBoolean(this, "Spoof opened debug hud", false, isEnabled = { calculationMethod.isSelected(1) })

    private val panel by lazy { ManagerPanel.list.filterIsInstance<PanelGraph>().first { it.graph == this } }

    fun shouldSpoof(): Boolean {
        if(!panel.opened)
            return false

        if(!spoofOpenedDebugHud.isEnabled())
            return false

        return spoofOpenedDebugHud.value
    }

    init {
        EventDispatcher.apply {
            add(EventPacket::class.java) { event ->
                if(event.type == EventPacket.Type.RECEIVE)
                    when {
                        calculationMethod.isSelected(0) -> {
                            if (event.packet is PlayerListS2CPacket)
                                if (event.packet.actions.contains(PlayerListS2CPacket.Action.ADD_PLAYER) || event.packet.actions.contains(PlayerListS2CPacket.Action.UPDATE_LATENCY))
                                    event.packet.entries.firstOrNull { it.profile?.id?.equals(mc.player?.uuid) == true }?.also {
                                        add(it.latency)
                                    }
                        }
                        calculationMethod.isSelected(1) -> {
                            if (event.packet is PingResultS2CPacket)
                                add(Util.getMeasuringTimeMs() - event.packet.startTime)
                        }
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