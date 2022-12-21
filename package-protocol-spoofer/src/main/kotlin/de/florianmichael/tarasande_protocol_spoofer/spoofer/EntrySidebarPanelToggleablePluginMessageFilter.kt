package de.florianmichael.tarasande_protocol_spoofer.spoofer

import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueTextList
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.EntrySidebarPanelToggleable
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.ManagerEntrySidebarPanel
import su.mandora.event.EventDispatcher

class EntrySidebarPanelToggleablePluginMessageFilter(sidebar: ManagerEntrySidebarPanel) : EntrySidebarPanelToggleable(sidebar, "Plugin message filter", "Spoofer") {

    private val filterType = ValueMode(this, "Filter type", false, "Contains", "Equals")
    private val channels = ValueTextList(this, "Channels", mutableListOf("fabric"))
    private val ignoreCase = ValueBoolean(this, "Ignore case", true)

    init {
        EventDispatcher.add(EventPacket::class.java) {
            if (!state.value) return@add

            if (it.type == EventPacket.Type.SEND && it.packet is CustomPayloadC2SPacket) {
                val packetChannel = (it.packet as CustomPayloadC2SPacket).channel.toString()

                for (channel in channels.value) {
                    if (if (filterType.isSelected(0)) packetChannel.contains(channel, ignoreCase.value) else packetChannel.equals(channel, ignoreCase.value)) {
                        it.cancelled = true
                        break
                    }
                }
            }
        }
    }
}
