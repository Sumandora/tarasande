package net.tarasandedevelopment.tarasande_protocol_hack.platform.multiplayerfeature

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.viaprotocolhack.util.VersionList
import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.system.feature.multiplayerfeaturesystem.MultiplayerFeature
import net.tarasandedevelopment.tarasande.system.feature.multiplayerfeaturesystem.MultiplayerFeatureCategory
import net.tarasandedevelopment.tarasande.system.feature.multiplayerfeaturesystem.MultiplayerFeatureSelection
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterParentValues
import net.tarasandedevelopment.tarasande_protocol_hack.TarasandeProtocolHack
import net.tarasandedevelopment.tarasande_protocol_hack.extension.getSpecialName
import net.tarasandedevelopment.tarasande_protocol_hack.platform.ProtocolHackValues

class MultiplayerFeatureSelectionProtocolHack(private val tarasandeProtocolHack: TarasandeProtocolHack) : MultiplayerFeatureSelection("Protocol Hack", MultiplayerFeatureCategory.PROTOCOL_HACK, VersionList.PROTOCOLS.map { it.getSpecialName() }, ProtocolVersion.getProtocol(tarasandeProtocolHack.targetVersion()).getSpecialName()) {

    override fun onClick(newValue: String) {
        tarasandeProtocolHack.apply {
            val newProtocol = VersionList.PROTOCOLS.first { it.getSpecialName() == newValue }.version.toDouble()
            if (version.value != newProtocol) {
                version.value = newProtocol
                update(ProtocolVersion.getProtocol(version.value.toInt()))
            }
        }
    }
}

class MultiplayerFeatureProtocolHackValues : MultiplayerFeature("Values", MultiplayerFeatureCategory.PROTOCOL_HACK) {

    override fun onClick(mouseButton: Int) {
        MinecraftClient.getInstance().setScreen(ScreenBetterParentValues(MinecraftClient.getInstance().currentScreen!!, "Protocol Hack Values", ProtocolHackValues))
    }
}
