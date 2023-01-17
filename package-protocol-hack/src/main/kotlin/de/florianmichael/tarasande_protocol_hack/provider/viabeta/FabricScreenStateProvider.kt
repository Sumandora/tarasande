package de.florianmichael.tarasande_protocol_hack.provider.viabeta

import de.florianmichael.viabeta.protocol.beta.protocolb1_8_0_1tob1_7_0_3.provider.ScreenStateProvider
import net.minecraft.client.gui.screen.DownloadingTerrainScreen
import net.tarasandedevelopment.tarasande.mc

class FabricScreenStateProvider : ScreenStateProvider() {

    override fun isDownloadingTerrain() = mc.currentScreen is DownloadingTerrainScreen
}
