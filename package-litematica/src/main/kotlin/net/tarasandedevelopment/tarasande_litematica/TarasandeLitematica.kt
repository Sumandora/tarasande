package net.tarasandedevelopment.tarasande_litematica

import net.fabricmc.api.ClientModInitializer
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande_litematica.generator.ManagerGenerator
import net.tarasandedevelopment.tarasande_litematica.panel.PanelLitematicaGenerators
import su.mandora.event.EventDispatcher

class TarasandeLitematica : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            TarasandeMain.managerPanel().add(PanelLitematicaGenerators(ManagerGenerator()))
        }
    }
}
