package su.mandora.tarasande.module.misc

import net.minecraft.client.gui.screen.DownloadingTerrainScreen
import net.minecraft.network.ClientConnection
import net.minecraft.network.NetworkState
import net.minecraft.network.Packet
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.event.Priority
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventPacket
import su.mandora.tarasande.event.EventUpdate
import su.mandora.tarasande.mixin.accessor.IClientConnection
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueMode
import su.mandora.tarasande.value.ValueNumber
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

class ModuleBlink : Module("Blink", "Delays packets", ModuleCategory.MISC) {

    private val affectedPackets = ValueMode(this, "Affected packets", true, "Serverbound", "Clientbound")
    private val pulse = ValueBoolean(this, "Pulse", false)
    private val pulseDelay = object : ValueNumber(this, "Pulse delay", 0.0, 500.0, 1000.0, 1.0) {
        override fun isEnabled() = pulse.value
    }

    private val packets = CopyOnWriteArrayList<Pair<Packet<*>, EventPacket.Type>>()
    private val timeUtil = TimeUtil()

    @Priority(9999)
    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventPacket -> {
                if (event.cancelled) return@Consumer
                if (event.packet != null) {
                    if (mc.networkHandler?.connection == null ||
                        (mc.networkHandler?.connection as IClientConnection).channel.attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get() != NetworkState.PLAY ||
                        ((event.type == EventPacket.Type.RECEIVE && event.packet is DisconnectS2CPacket) || (!pulse.value && mc.currentScreen is DownloadingTerrainScreen))) {
                        this.switchState()
                        return@Consumer
                    }
                    if (pulse.value && mc.currentScreen is DownloadingTerrainScreen) {
                        onDisable()
                        return@Consumer
                    }
                    if (affectedPackets.isSelected(event.type.ordinal)) {
                        packets.add(Pair(event.packet, event.type))
                        event.cancelled = true
                    }
                }
            }
            is EventUpdate -> {
                if (event.state == EventUpdate.State.PRE) {
                    if (pulse.value) {
                        if (timeUtil.hasReached(pulseDelay.value.toLong())) {
                            onDisable()
                            timeUtil.reset()
                        }
                    }
                }
            }
        }
    }

    override fun onDisable() {
        val copy = ArrayList(packets) // sync
        packets.clear()
        if (mc.networkHandler?.connection?.isOpen!!) {
            for (pair in copy) {
                when (pair.second) {
                    EventPacket.Type.SEND -> (mc.networkHandler?.connection as IClientConnection).forceSend(pair.first)
                    EventPacket.Type.RECEIVE ->
                        if (mc.networkHandler?.connection?.packetListener is ClientPlayPacketListener)
                            (pair.first as Packet<ClientPlayPacketListener>).apply(mc.networkHandler?.connection?.packetListener as ClientPlayPacketListener)
                }
            }
        }
    }
}
