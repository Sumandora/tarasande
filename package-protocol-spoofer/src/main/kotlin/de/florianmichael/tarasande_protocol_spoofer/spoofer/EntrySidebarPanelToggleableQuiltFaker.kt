package de.florianmichael.tarasande_protocol_spoofer.spoofer

import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket
import net.minecraft.util.Identifier
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.SidebarEntryToggleable
import net.tarasandedevelopment.tarasande.util.extension.mc
import su.mandora.event.EventDispatcher

class EntrySidebarPanelToggleableQuiltFaker : SidebarEntryToggleable("Quilt Faker", "Spoofer") {

    private val quiltHandshake = Identifier("registry_sync/handshake")

    init {
        EventDispatcher.add(EventPacket::class.java) {
            if (!enabled.value) return@add

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
}
