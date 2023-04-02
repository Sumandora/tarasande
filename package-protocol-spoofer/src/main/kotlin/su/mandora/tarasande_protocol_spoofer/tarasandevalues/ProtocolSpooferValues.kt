package su.mandora.tarasande_protocol_spoofer.tarasandevalues

import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket
import net.minecraft.util.Identifier
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import su.mandora.tarasande_protocol_spoofer.TarasandeProtocolSpoofer

val quiltHandshake = Identifier("registry_sync/handshake")
object ProtocolSpooferValues {

    init {
        ValueButtonOwnerValues(this, "Client brand spoofer", ClientBrandSpoofer)
        ValueButtonOwnerValues(this, "Plugin message filter", PluginMessageFilter)
        ValueButtonOwnerValues(this, "Resource pack spoofer", ResourcePackSpoofer)
        if (TarasandeProtocolSpoofer.viaFabricPlusLoaded) {
            ValueButtonOwnerValues(this, "Forge protocol spoofer", ForgeProtocolSpoofer)
            ValueButtonOwnerValues(this, "Tesla client spoofer", TeslaClientSpoofer)
        }
    }

    private val spoofQuiltProtocol = ValueBoolean(this, "Spoof Quilt protocol", false)
    init {
        EventDispatcher.add(EventPacket::class.java) {
            if (!spoofQuiltProtocol.value) return@add

            if (it.type == EventPacket.Type.RECEIVE && it.packet is CustomPayloadS2CPacket) {
                if ((it.packet as CustomPayloadS2CPacket).channel == quiltHandshake) {
                    val data = (it.packet as CustomPayloadS2CPacket).data

                    var count = data.readVarInt()
                    var highestSupported = -1

                    while (--count > 0) {
                        val version = data.readVarInt()
                        if (version > highestSupported) {
                            highestSupported = version
                        }
                    }

                    val buffer = PacketByteBuf(Unpooled.buffer())
                    buffer.writeVarInt(highestSupported)

                    mc.networkHandler!!.sendPacket(CustomPayloadC2SPacket(quiltHandshake, buffer))
                }
            }
        }
    }

    init {
        ValueButtonOwnerValues(this, "Vivecraft spoofer", VivecraftSpoofer)
        ValueButtonOwnerValues(this, "Mystery mod spoofer", MysteryModSpoofer)
        ValueButtonOwnerValues(this, "HA Proxy protocol", HAProxyProtocol)
        ValueButtonOwnerValues(this, "BungeeCord IP forwarding", BungeeCordIPForwarding)
    }
}
