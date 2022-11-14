package net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.impl

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.viaprotocolhack.util.VersionList
import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.protocolhack.extension.getSpecialName
import net.tarasandedevelopment.tarasande.protocolhack.platform.ProtocolHackValues
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterParentPopupSettings
import net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.MultiplayerFeature
import net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.MultiplayerFeatureCategory
import net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.MultiplayerFeatureSelection

class MultiplayerFeatureProtocolHack : MultiplayerFeatureSelection("Protocol Hack", MultiplayerFeatureCategory.PROTOCOL_HACK, VersionList.getProtocols().map { it.getSpecialName() }, ProtocolVersion.getProtocol(TarasandeMain.protocolHack().version.value.toInt()).getSpecialName()) {

    override fun onClick(newValue: String) {
        TarasandeMain.instance.protocolHack.version.value = VersionList.getProtocols().first { it.getSpecialName() == newValue }.version.toDouble()
        TarasandeMain.instance.protocolHack.update(ProtocolVersion.getProtocol(TarasandeMain.instance.protocolHack.version.value.toInt()))
    }
}

class MultiplayerFeatureProtocolHackSettings : MultiplayerFeature("Settings", MultiplayerFeatureCategory.PROTOCOL_HACK) {

    override fun onClick(mouseButton: Int) {
        MinecraftClient.getInstance().setScreen(ScreenBetterParentPopupSettings(MinecraftClient.getInstance().currentScreen!!, "ProtocolHackValues", ProtocolHackValues))
    }
}
