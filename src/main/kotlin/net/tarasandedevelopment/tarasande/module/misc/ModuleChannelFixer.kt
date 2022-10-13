package net.tarasandedevelopment.tarasande.module.misc

import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.mixin.accessor.ICustomPayloadC2SPacket
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueText
import java.nio.charset.StandardCharsets
import java.util.function.Consumer

class ModuleChannelFixer : Module("Channel fixer", "Spoof client brand.", ModuleCategory.MISC) {

    val clientbrand = ValueText(this, "Clientbrand", "vanilla")

    val eventConsumer = Consumer<Event> {
        if (it is EventPacket) {
            if (it.type == EventPacket.Type.SEND && it.packet is CustomPayloadC2SPacket) {
                if (it.packet.channel == CustomPayloadC2SPacket.BRAND) {
                    (it.packet as ICustomPayloadC2SPacket).setData(PacketByteBuf(Unpooled.buffer()).writeString(this.clientbrand.value))
                }
            }
        }
    }
}
