package net.tarasandedevelopment.tarasande.module.qualityoflife

import net.minecraft.client.gui.screen.DownloadingTerrainScreen
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventChangeScreen

class ModuleInstantWorld : Module("Instant world", "Reverts 22w12a -> 22w13a loading conditions", ModuleCategory.QUALITY_OF_LIFE) {

    init {
        registerEvent(EventChangeScreen::class.java) { event ->
            if (event.newScreen is DownloadingTerrainScreen) {
                event.newScreen = null
            }
        }
    }
}