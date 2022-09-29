package de.florianmichael.tarasande.module.qol

import de.florianmichael.tarasande.event.EventChangeScreen
import net.minecraft.client.gui.screen.DownloadingTerrainScreen
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import java.util.function.Consumer

class ModuleInstantWorld : Module("Instant World", "Reverts 22w12a -> 22w13a loading conditions", ModuleCategory.QOL) {

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventChangeScreen -> {
                if (event.newScreen is DownloadingTerrainScreen) {
                    event.newScreen = null
                    event.cancelled = true
                }
            }
        }
    }
}