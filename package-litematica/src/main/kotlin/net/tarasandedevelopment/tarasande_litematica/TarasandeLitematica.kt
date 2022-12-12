package net.tarasandedevelopment.tarasande_litematica

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.ValueButton
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.ValueSpacer
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.PanelElements
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterParentValues
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
