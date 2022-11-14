package net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.impl.proxy.multiplayerfeature

import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.MultiplayerFeature
import net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.MultiplayerFeatureCategory
import net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.impl.proxy.ScreenBetterProxy

class MultiplayerFeatureProxySystem : MultiplayerFeature("Proxy System", MultiplayerFeatureCategory.GENERAL) {

    override fun onClick(mouseButton: Int) {
        MinecraftClient.getInstance().setScreen(ScreenBetterProxy(MinecraftClient.getInstance().currentScreen))
    }
}