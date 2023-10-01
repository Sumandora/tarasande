package su.mandora.tarasande.system.feature.modulesystem.impl.misc

import net.minecraft.network.NetworkState
import net.minecraft.network.listener.PacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.injection.accessor.IClientConnection
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import java.util.*

class ModulePingSpoof : Module("Ping spoof", "Increases ping artificially", ModuleCategory.MISC) {

    private val affectedPackets = ValueMode(this, "Affected packets", false, "Serverbound", "Clientbound") // Both is not needed and would break stuff
    private val packetTypes = ValueMode(this, "Packet types", true, "Keep alive", "Ping", "Ping queries")

    private val latency = ValueNumber(this, "Latency", 0.0, 200.0, 500.0, 10.0)

    init {
        affectedPackets.select(0)
        packetTypes.select(0)
    }

    private val queuedPackets = Collections.synchronizedList(ArrayList<Triple<Packet<*>, EventPacket.Type, Long>>())

    private fun isPacketTargeted(packet: Packet<*>): Boolean {
        if (packetTypes.isSelected(0))
            if (packet is KeepAliveC2SPacket || packet is KeepAliveS2CPacket)
                return true
        if (packetTypes.isSelected(1))
            if (packet is CommonPingS2CPacket || packet is CommonPongC2SPacket)
                return true
        if (packetTypes.isSelected(2))
            if (packet is QueryPingC2SPacket || packet is PingResultS2CPacket)
                return true
        return false
    }

    private fun processPacket(packetTriple: Triple<Packet<*>, EventPacket.Type, Long>) {
        when (packetTriple.second) {
            EventPacket.Type.SEND -> (mc.networkHandler!!.connection as IClientConnection).tarasande_forceSend(packetTriple.first)
            EventPacket.Type.RECEIVE ->
                try {
                    @Suppress("UNCHECKED_CAST")
                    (packetTriple.first as Packet<PacketListener>).apply(mc.networkHandler!!.connection.packetListener)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
        }
    }

    init {
        registerEvent(EventPacket::class.java, 9999) { event ->
            if (event.cancelled || event.packet == null)
                return@registerEvent

            if (mc.networkHandler?.state != NetworkState.PLAY && mc.networkHandler?.state != NetworkState.CONFIGURATION) {
                queuedPackets.clear() // Packets don't matter anymore
                return@registerEvent
            }

            if (!affectedPackets.isSelected(event.type.ordinal))
                return@registerEvent

            // Put this here to prevent ConcurrentModificationException
            queuedPackets.removeIf {
                if (it.third <= System.currentTimeMillis()) {
                    processPacket(it)
                    return@removeIf true
                }
                return@removeIf false
            }

            if (!isPacketTargeted(event.packet))
                return@registerEvent

            queuedPackets.add(Triple(event.packet, event.type, System.currentTimeMillis() + latency.value.toLong()))
            event.cancelled = true
        }
    }

    override fun onDisable() {
        if (mc.networkHandler?.connection?.isOpen == true)
            queuedPackets.forEach(::processPacket)
        queuedPackets.clear()
    }
}