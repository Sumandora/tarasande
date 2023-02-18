package de.florianmichael.tarasande_protocol_hack.tarasande.sidebar

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.tarasande_protocol_hack.TarasandeProtocolHack
import de.florianmichael.tarasande_protocol_hack.tarasande.values.ProtocolHackValues
import de.florianmichael.vialoadingbase.api.version.InternalProtocolList
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.SidebarEntrySelection

class SidebarEntrySelectionProtocolHack : SidebarEntrySelection("Protocol Hack", "Protocol Hack", InternalProtocolList.getProtocols().map { it.name }) {

    override fun onClick() {
        TarasandeProtocolHack.update(selectedProtocol(), ProtocolHackValues.autoChangeValuesDependentOnVersion.value)
    }

    fun selectedProtocol(): ProtocolVersion = InternalProtocolList.fromProtocolId(InternalProtocolList.getProtocols().first { it.name == value.getSelected() }.version)
}
