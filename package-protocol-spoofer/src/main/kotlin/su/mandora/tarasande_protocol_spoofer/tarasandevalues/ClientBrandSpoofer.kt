package su.mandora.tarasande_protocol_spoofer.tarasandevalues

import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueText

object ClientBrandSpoofer {
    private val enabled = ValueBoolean(this, "Enabled", false)
    private val clientBrand = ValueText(this, "Client brand", "vanilla")

    init {
        EventDispatcher.add(EventPacket::class.java) {
            if (!enabled.value) return@add

            if (it.type == EventPacket.Type.SEND && it.packet is CustomPayloadC2SPacket) {
                if ((it.packet as CustomPayloadC2SPacket).channel == CustomPayloadC2SPacket.BRAND) {
                    (it.packet as CustomPayloadC2SPacket).data = PacketByteBuf(Unpooled.buffer()).writeString(clientBrand.value)
                }
            }
        }
    }
}
