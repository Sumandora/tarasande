package net.tarasandedevelopment.tarasande_protocol_hack.provider.viabeta

import de.florianmichael.viabeta.protocol.beta.protocolb1_8_0_1tob1_7_0_3.provider.ScreenStateProvider
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.DownloadingTerrainScreen

class FabricScreenStateProvider : ScreenStateProvider() {

    override fun isDownloadingTerrain() = MinecraftClient.getInstance().currentScreen is DownloadingTerrainScreen
}
