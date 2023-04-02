package su.mandora.tarasande_protocol_spoofer.tarasandevalues

import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueTextList

object PluginMessageFilter {
    private val enabled = ValueBoolean(this, "Enabled", false)

    private val filterType = ValueMode(this, "Filter type", false, "Contains", "Equals")
    private val channels = ValueTextList(this, "Channels", arrayListOf("fabric"))
    private val ignoreCase = ValueBoolean(this, "Ignore case", true)

    init {
        EventDispatcher.add(EventPacket::class.java) {
            if (!enabled.value) return@add

            if (it.type == EventPacket.Type.SEND && it.packet is CustomPayloadC2SPacket) {
                val packetChannel = (it.packet as CustomPayloadC2SPacket).channel.toString()

                for (channel in channels.entries()) {
                    if (when {
                            filterType.isSelected(0) -> packetChannel.contains(channel, ignoreCase.value)
                            filterType.isSelected(1) -> packetChannel.equals(channel, ignoreCase.value)
                            else -> false
                        }) {
                        it.cancelled = true
                        break
                    }
                }
            }
        }
    }
}