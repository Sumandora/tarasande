package net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.impl.accountmanager.multiplayerfeature

import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.impl.accountmanager.ScreenBetterSlotListAccountManager
import net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.MultiplayerFeature
import net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.MultiplayerFeatureCategory

class MultiplayerFeatureAccountManager : MultiplayerFeature("Account Manager", MultiplayerFeatureCategory.GENERAL) {

    val screenBetterSlotListAccountManager = ScreenBetterSlotListAccountManager()

    override fun onClick(mouseButton: Int) {
        MinecraftClient.getInstance().setScreen(this.screenBetterSlotListAccountManager.apply { prevScreen = MinecraftClient.getInstance().currentScreen })
    }
}