package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.sidebar

import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueText
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.SidebarEntryToggleable
import su.mandora.event.EventDispatcher

class SidebarEntryToggleableClientBrandSpoofer : SidebarEntryToggleable( "Client brand spoofer", "Spoofer") {

    private val clientBrand = ValueText(this, "Client brand", "vanilla")

    init {
        EventDispatcher.add(EventPacket::class.java) {
            if (!enabled.value) return@add

            if (it.type == EventPacket.Type.SEND && it.packet is CustomPayloadC2SPacket) {
                if (it.packet.channel == CustomPayloadC2SPacket.BRAND) {
                    it.packet.data = PacketByteBuf(Unpooled.buffer()).writeString(clientBrand.value)
                }
            }
        }
    }
}
