package de.florianmichael.tarasande_litematica

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.ManagerPanel
import de.florianmichael.tarasande_litematica.generator.ManagerGenerator
import de.florianmichael.tarasande_litematica.panel.PanelLitematicaGenerators
import su.mandora.event.EventDispatcher
import java.util.logging.Logger

class TarasandeLitematica : ClientModInitializer {
    private val logger = Logger.getLogger("tarasande-litematica")!!

    override fun onInitializeClient() {
        if (!FabricLoader.getInstance().isModLoaded("litematica")) {
            logger.warning("tarasande Litematica is not designed to run without 'Litematica' installed")
            return
        }
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            ManagerPanel.add(PanelLitematicaGenerators(ManagerGenerator()))
        }
    }
}