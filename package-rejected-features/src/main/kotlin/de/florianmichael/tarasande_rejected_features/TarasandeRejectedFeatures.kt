package de.florianmichael.tarasande_rejected_features

import de.florianmichael.tarasande_rejected_features.command.CommandDeadByDaylightEscape
import de.florianmichael.tarasande_rejected_features.command.CommandSparkyHeal
import de.florianmichael.tarasande_rejected_features.information.*
import de.florianmichael.tarasande_rejected_features.module.ModuleAutoRescuePlatform
import de.florianmichael.tarasande_rejected_features.module.ModuleDropper
import de.florianmichael.tarasande_rejected_features.module.ModuleRoundedMovement
import de.florianmichael.tarasande_rejected_features.tarasandevalues.ClosedInventory
import net.fabricmc.api.ClientModInitializer
import net.tarasandedevelopment.tarasande.event.EventDispatcher
import net.tarasandedevelopment.tarasande.event.impl.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.system.feature.commandsystem.ManagerCommand
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.ManagerInformation

class TarasandeRejectedFeatures : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            ManagerModule.add(
                ModuleRoundedMovement(),
                ModuleAutoRescuePlatform(),
                ModuleDropper()
            )

            ManagerInformation.add(
                // Time
                InformationDate(),
                InformationTime(),

                // Features
                InformationFeaturesModules(),
                InformationFeaturesValues(),
                InformationFeaturesGraphs(),
                InformationFeaturesPackagesForTarasande(),

                // System
                InformationCPU(),
                InformationGPU(),

                // KeyBinds
                InformationKeyBinds(),

                // Server
                InformationLag(),
                InformationMovements(),

                // Connection
                InformationNettyLag()
            )

            ManagerCommand.add(
                CommandDeadByDaylightEscape(),
                CommandSparkyHeal()
            )

            ClosedInventory
        }
    }
}
