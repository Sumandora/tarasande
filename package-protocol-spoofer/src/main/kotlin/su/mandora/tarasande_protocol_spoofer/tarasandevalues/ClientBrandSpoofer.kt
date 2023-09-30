package su.mandora.tarasande_protocol_spoofer.tarasandevalues

import net.minecraft.network.packet.BrandCustomPayload
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket
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
                val packet = it.packet as CustomPayloadC2SPacket
                if(packet.payload is BrandCustomPayload)
                    packet.payload = BrandCustomPayload(clientBrand.value)
            }
        }
    }
}
