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
import su.mandora.tarasande.event.EventPollEvents
import su.mandora.tarasande.mixin.accessor.IClientConnection
import su.mandora.tarasande.value.ValueMode
import su.mandora.tarasande.value.ValueNumber
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

class ModuleLatency : Module("Latency", "Controls network latency", ModuleCategory.MISC) {

    private val affectedPackets = ValueMode(this, "Affected packets", true, "Serverbound", "Clientbound")
    private val latency = object : ValueNumber(this, "Latency", 0.0, 100.0, 1000.0, 1.0) {
        var prev = 0.0
        override fun onChange() {
            if (value < prev)
                onDisable()
            prev = value
        }
    }

    private val packets = CopyOnWriteArrayList<Triple<Long, Packet<*>, EventPacket.Type>>()

    @Priority(10000)
    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventPacket -> {
                if (event.cancelled) return@Consumer
                if (event.packet != null) {
                    if (mc.networkHandler?.connection == null ||
                        (mc.networkHandler?.connection as IClientConnection).tarasande_getChannel().attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get() != NetworkState.PLAY ||
                        ((event.type == EventPacket.Type.RECEIVE && event.packet is DisconnectS2CPacket) || mc.currentScreen is DownloadingTerrainScreen)) {
                        this.switchState()
                        return@Consumer
                    }
                    if (affectedPackets.isSelected(event.type.ordinal)) {
                        packets.add(Triple(System.currentTimeMillis() + latency.value.toLong(), event.packet, event.type))
                        event.cancelled = true
                    }
                }
            }
            is EventPollEvents -> { // runs more often than tick
                val copy = ArrayList(packets) // sync
                packets.removeIf { it.first < System.currentTimeMillis() }
                for (triple in copy) {
                    if (triple.first < System.currentTimeMillis()) {
                        when (triple.third) {
                            EventPacket.Type.SEND -> (mc.networkHandler?.connection as IClientConnection).tarasande_forceSend(triple.second)
                            EventPacket.Type.RECEIVE -> {
                                if (mc.networkHandler?.connection?.packetListener is ClientPlayPacketListener)
                                    (triple.second as Packet<ClientPlayPacketListener>).apply(mc.networkHandler?.connection?.packetListener as ClientPlayPacketListener)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDisable() {
        if (mc.networkHandler?.connection?.isOpen!!) {
            for (triple in packets) {
                when (triple.third) {
                    EventPacket.Type.SEND -> (mc.networkHandler?.connection as IClientConnection).tarasande_forceSend(triple.second)
                    EventPacket.Type.RECEIVE ->
                        if (mc.networkHandler?.connection?.packetListener is ClientPlayPacketListener)
                            (triple.second as Packet<ClientPlayPacketListener>).apply(mc.networkHandler?.connection?.packetListener as ClientPlayPacketListener)
                }
            }
        }
        packets.clear()
    }

}