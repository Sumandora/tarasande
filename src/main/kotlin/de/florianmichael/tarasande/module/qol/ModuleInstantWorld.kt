package de.florianmichael.tarasande.module.qol

import de.florianmichael.tarasande.event.EventChangeScreen
import net.minecraft.client.gui.screen.DownloadingTerrainScreen
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import java.util.function.Consumer

class ModuleInstantWorld : Module("Instant world", "Reverts 22w12a -> 22w13a loading conditions", ModuleCategory.QUALITY_OF_LIFE) {

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventChangeScreen -> {
                if (event.newScreen is DownloadingTerrainScreen) {
                    event.newScreen = null
                }
            }
        }
    }
}