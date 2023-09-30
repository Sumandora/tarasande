package su.mandora.tarasande_protocol_spoofer.tarasandevalues

import net.minecraft.util.Identifier
import su.mandora.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues

val quiltHandshake = Identifier("registry_sync/handshake")

object ProtocolSpooferValues {

    init {
        ValueButtonOwnerValues(this, "Client brand spoofer", ClientBrandSpoofer)
        ValueButtonOwnerValues(this, "Plugin message filter", PluginMessageFilter)
        ValueButtonOwnerValues(this, "HA Proxy protocol", HAProxyProtocol)
        ValueButtonOwnerValues(this, "BungeeCord IP forwarding", BungeeCordIPForwarding)
    }
}
