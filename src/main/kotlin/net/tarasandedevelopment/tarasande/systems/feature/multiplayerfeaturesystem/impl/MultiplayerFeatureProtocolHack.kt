package net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.impl

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.viaprotocolhack.util.VersionList
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.protocolhack.extension.getSpecialName
import net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.MultiplayerFeatureCategory
import net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.MultiplayerFeatureSelection

class MultiplayerFeatureProtocolHack : MultiplayerFeatureSelection("Protocol Hack", MultiplayerFeatureCategory.PROTOCOL_HACK, VersionList.getProtocols().map { it.getSpecialName() }, ProtocolVersion.getProtocol(TarasandeMain.protocolHack().version.value.toInt()).getSpecialName()) {

    override fun onChange(newValue: String) {
        TarasandeMain.instance.protocolHack.version.value = VersionList.getProtocols().first { it.getSpecialName() == newValue }.version.toDouble()
    }
}
