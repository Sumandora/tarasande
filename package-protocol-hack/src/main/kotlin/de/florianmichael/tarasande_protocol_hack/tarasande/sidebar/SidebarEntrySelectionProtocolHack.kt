package de.florianmichael.tarasande_protocol_hack.tarasande.sidebar

import de.florianmichael.tarasande_protocol_hack.TarasandeProtocolHack
import de.florianmichael.tarasande_protocol_hack.util.values.ProtocolHackValues
import de.florianmichael.vialoadingbase.api.version.InternalProtocolList
import net.minecraft.SharedConstants
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.SidebarEntrySelection

class SidebarEntrySelectionProtocolHack : SidebarEntrySelection("Protocol Hack", "Protocol Hack", InternalProtocolList.getProtocols().map { it.name }) {

    val version = ValueNumber(TarasandeProtocolHack, "Protocol", Double.MIN_VALUE, SharedConstants.getProtocolVersion().toDouble(), Double.MAX_VALUE, 1.0, exceed = false)

    override fun onClick(newValue: String) {
        val newProtocol = InternalProtocolList.getProtocols().first { it.name == newValue }.version.toDouble()
        if (version.value != newProtocol) {
            version.value = newProtocol
            TarasandeProtocolHack.update(
                InternalProtocolList.fromProtocolId(version.value.toInt()),
                ProtocolHackValues.autoChangeValuesDependentOnVersion.value
            )
        }
    }

    override fun isSelected(value: String): Boolean {
        val protocol = InternalProtocolList.getProtocols().first { it.name == value }.version.toDouble()
        return version.value == protocol
    }
}
