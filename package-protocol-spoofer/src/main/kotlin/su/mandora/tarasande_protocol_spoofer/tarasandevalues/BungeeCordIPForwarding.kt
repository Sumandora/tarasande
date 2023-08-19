package su.mandora.tarasande_protocol_spoofer.tarasandevalues

import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueText
import java.util.*

object BungeeCordIPForwarding {
    private val enabled = ValueBoolean(this, "Enabled", false)
    private val endIP = ValueText(this, "End IP", "127.0.0.1")
    private val customUUID = ValueBoolean(this, "Custom UUID", false)
    private val uuid = ValueText(this, "UUID", UUID.randomUUID().toString(), isEnabled = { customUUID.value })

    private const val NULL_TERMINATOR = '\u0000'
    private fun stripID(input: String) = input.replace("-", "")

    init {
        EventDispatcher.add(EventPacket::class.java) { event ->
            if (event.type != EventPacket.Type.SEND || event.packet !is HandshakeC2SPacket)
                return@add
            if (enabled.value) {
                val uuid =
                    if (this.customUUID.value)
                        this.uuid.value
                    else
                        mc.session.uuid

                (event.packet as HandshakeC2SPacket).address += NULL_TERMINATOR + endIP.value + NULL_TERMINATOR + stripID(uuid)
            }
        }
    }
}
