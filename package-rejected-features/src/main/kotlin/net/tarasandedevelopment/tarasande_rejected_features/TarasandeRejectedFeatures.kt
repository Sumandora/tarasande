package net.tarasandedevelopment.tarasande_rejected_features

import net.fabricmc.api.ClientModInitializer
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande_rejected_features.information.*
import net.tarasandedevelopment.tarasande_rejected_features.module.ModuleDeadByDaylightEscape
import net.tarasandedevelopment.tarasande_rejected_features.module.ModuleFurnaceProgress
import net.tarasandedevelopment.tarasande_rejected_features.module.ModuleRoundedMovement
import su.mandora.event.EventDispatcher

class TarasandeRejectedFeatures : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            TarasandeMain.managerModule().add(
                ModuleDeadByDaylightEscape(),
                ModuleRoundedMovement(),
                ModuleFurnaceProgress()
            )

            TarasandeMain.managerInformation().add(
                // Time
                InformationDate(),
                InformationTime(),

                // Features
                InformationFeaturesModules(),
                InformationFeaturesValues(),
                InformationFeaturesGraphs(),
                InformationFeaturesPackagesForTarasande(),

                // KeyBinds
                InformationKeyBinds()
            )
        }
    }
}
