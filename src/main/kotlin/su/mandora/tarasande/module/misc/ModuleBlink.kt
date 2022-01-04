package su.mandora.tarasande.module.misc

import net.minecraft.network.ClientConnection
import net.minecraft.network.NetworkState
import net.minecraft.network.Packet
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket
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

    private val cancelledPackets = ValueMode(this, "Cancelled packets", true, "Serverbound", "Clientbound")
    private val pulse = ValueBoolean(this, "Pulse", false)
    private val pulseDelay = object : ValueNumber(this, "Pulse delay", 0.0, 500.0, 1000.0, 1.0) {
        override fun isVisible(): Boolean {
            return pulse.value
        }
    }

    private val packets = CopyOnWriteArrayList<Pair<Packet<*>, EventPacket.Type>>()
    private val timeUtil = TimeUtil()

    @Priority(9999) // always be the last module messing with packets because otherwise we might run into problems, where some packet is going to be cancelled later
    val eventConsumer = Consumer<Event> { event ->
        if (event is EventPacket) {
            if (event.cancelled) return@Consumer
            if (event.packet != null) {
                if (NetworkState.getPacketHandlerState(event.packet) != NetworkState.PLAY) return@Consumer
                when (event.type) {
                    EventPacket.Type.SEND -> {
                        if (event.packet is HandshakeC2SPacket) {
                            // We are reconnecting
                            packets.clear()
                        } else if (cancelledPackets.isSelected(0)) {
                            packets.add(Pair(event.packet, event.type))
                            event.setCancelled()
                        }
                    }
                    EventPacket.Type.RECEIVE -> {
                        if (event.packet is DisconnectS2CPacket) {
                            // We fucked up
                            packets.clear()
                        } else if (cancelledPackets.isSelected(1)) {
                            packets.add(Pair(event.packet, event.type))
                            event.setCancelled()
                        }
                    }
                }
            }
        }
        if (event is EventUpdate && event.state == EventUpdate.State.PRE) {
            if (timeUtil.hasReached(pulseDelay.value.toLong())) {
                onDisable()
                timeUtil.reset()
            }
        }
    }

    override fun onDisable() {
        val copy = ArrayList<Pair<Packet<*>, EventPacket.Type>>(packets) // sync
        packets.clear()
        if ((mc.networkHandler?.connection as IClientConnection).channel.attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get() != NetworkState.PLAY) {
            return
        }
        for (pair in copy) {
            when (pair.second) {
                EventPacket.Type.SEND -> mc.networkHandler?.sendPacket(pair.first)
                EventPacket.Type.RECEIVE -> if (mc.networkHandler?.connection?.packetListener is ClientPlayPacketListener && pair.first.javaClass.genericSuperclass.javaClass === ClientPlayPacketListener::class.java) (pair.first as Packet<ClientPlayPacketListener>).apply(mc.networkHandler?.connection?.packetListener as ClientPlayPacketListener)
            }
        }
    }
}
