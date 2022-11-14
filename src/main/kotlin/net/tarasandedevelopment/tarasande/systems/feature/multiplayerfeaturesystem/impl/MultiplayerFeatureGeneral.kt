package net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.impl

import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.MultiplayerFeature
import net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.MultiplayerFeatureCategory
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl.minecraftmenus.accountmanager.ScreenBetterSlotListAccountManager
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl.minecraftmenus.proxy.ScreenBetterProxy

class MultiplayerFeatureGeneralAccountManager : MultiplayerFeature("Account Manager", MultiplayerFeatureCategory.GENERAL) {

    val screenBetterSlotListAccountManager = ScreenBetterSlotListAccountManager()

    override fun onClick(mouseButton: Int) {
        MinecraftClient.getInstance().setScreen(this.screenBetterSlotListAccountManager.apply { prevScreen = MinecraftClient.getInstance().currentScreen })
    }
}

class MultiplayerFeatureGeneralProxySystem : MultiplayerFeature("Proxy System", MultiplayerFeatureCategory.GENERAL) {

    override fun onClick(mouseButton: Int) {
        MinecraftClient.getInstance().setScreen(ScreenBetterProxy(MinecraftClient.getInstance().currentScreen))
    }
}