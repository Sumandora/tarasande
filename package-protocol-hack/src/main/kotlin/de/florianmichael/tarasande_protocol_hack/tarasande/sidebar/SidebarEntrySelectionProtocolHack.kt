package de.florianmichael.tarasande_protocol_hack.tarasande.sidebar

import de.florianmichael.tarasande_protocol_hack.TarasandeProtocolHack
import de.florianmichael.tarasande_protocol_hack.util.values.ProtocolHackValues
import de.florianmichael.vialoadingbase.util.VersionListEnum
import net.minecraft.SharedConstants
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.SidebarEntrySelection

class SidebarEntrySelectionProtocolHack : SidebarEntrySelection("Protocol Hack", "Protocol Hack", VersionListEnum.RENDER_VERSIONS.map { it.getName() }) {

    val version = ValueNumber(this, "Protocol", Double.MIN_VALUE, SharedConstants.getProtocolVersion().toDouble(), Double.MAX_VALUE, 1.0, exceed = false)

    override fun onClick(newValue: String) {
        val newProtocol = VersionListEnum.RENDER_VERSIONS.first { it.getName() == newValue }.version.toDouble()
        if (version.value != newProtocol) {
            version.value = newProtocol
            TarasandeProtocolHack.update(
                VersionListEnum.fromProtocolId(version.value.toInt()),
                ProtocolHackValues.autoChangeValuesDependentOnVersion.value
            )
        }
    }

    override fun isSelected(value: String): Boolean {
        val protocol = VersionListEnum.RENDER_VERSIONS.first { it.getName() == value }.version.toDouble()
        return version.value == protocol
    }
}
