package net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.*
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.ScreenExtension
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl.minecraftmenus.accountmanager.ScreenBetterSlotListAccountManager
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl.minecraftmenus.proxy.ScreenBetterProxy

class ScreenExtensionMinecraftMenusAccountManager : ScreenExtension("Account manager", TitleScreen::class.java, MultiplayerScreen::class.java) {

    val screenBetterSlotListAccountManager = ScreenBetterSlotListAccountManager()

    override fun onClick(current: Screen) {
        MinecraftClient.getInstance().setScreen(this.screenBetterSlotListAccountManager.apply { prevScreen = MinecraftClient.getInstance().currentScreen })
    }
}

class ScreenExtensionMinecraftMenusProxySystem : ScreenExtension("Proxy system", TitleScreen::class.java, MultiplayerScreen::class.java) {

    override fun onClick(current: Screen) {
        MinecraftClient.getInstance().setScreen(ScreenBetterProxy(MinecraftClient.getInstance().currentScreen))
    }
}

class ScreenExtensionMinecraftMenusSleepingChat : ScreenExtension("Client wakeup", SleepingChatScreen::class.java) {

    override fun onClick(current: Screen) {
        MinecraftClient.getInstance().player?.wakeUp()
        MinecraftClient.getInstance().setScreen(null)
    }
}

class ScreenExtensionMinecraftMenusDeath : ScreenExtension("Force respawn", DeathScreen::class.java) {

    override fun onClick(current: Screen) {
        MinecraftClient.getInstance().player?.requestRespawn()
        MinecraftClient.getInstance().setScreen(null)
    }
}
