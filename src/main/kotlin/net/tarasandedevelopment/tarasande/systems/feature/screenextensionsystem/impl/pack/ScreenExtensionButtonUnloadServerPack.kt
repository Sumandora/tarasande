package net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl.pack

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.pack.PackScreen
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.ScreenExtensionButton


class ScreenExtensionButtonUnloadServerPack : ScreenExtensionButton<PackScreen>("Unload server pack", PackScreen::class.java) {
    override fun onClick(current: PackScreen) {
        MinecraftClient.getInstance().resourcePackProvider.clear()
    }

    override fun isVisible() = MinecraftClient.getInstance().resourcePackProvider != null
}