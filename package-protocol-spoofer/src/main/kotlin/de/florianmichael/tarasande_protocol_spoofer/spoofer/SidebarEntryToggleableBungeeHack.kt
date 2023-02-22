package de.florianmichael.tarasande_protocol_spoofer.spoofer

import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket
import net.tarasandedevelopment.tarasande.event.EventDispatcher
import net.tarasandedevelopment.tarasande.event.impl.EventPacket
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueText
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.SidebarEntryToggleable
import java.util.*

class SidebarEntryToggleableBungeeHack : SidebarEntryToggleable("Bungee hack", "Spoofer") {

    private val endIP = ValueText(this, "End IP", "127.0.0.1")
    private val customUUID = ValueBoolean(this, "Custom UUID", false)
    private val uuid = ValueText(this, "UUID", UUID.randomUUID().toString(), isEnabled = { customUUID.value })

    private val zero = "\u0000"
    private fun stripID(input: String) = input.replace("-", "")

    init {
        EventDispatcher.add(EventPacket::class.java) { event ->
            if (event.type != EventPacket.Type.SEND) return@add
            if (event.packet !is HandshakeC2SPacket) return@add
            if (enabled.value) {
                var uuid = mc.session.uuid
                if (this.customUUID.value)
                    uuid = this.uuid.value

                (event.packet as HandshakeC2SPacket).address += this.zero + this.endIP.value + this.zero + this.stripID(uuid)
            }
        }
    }
}
