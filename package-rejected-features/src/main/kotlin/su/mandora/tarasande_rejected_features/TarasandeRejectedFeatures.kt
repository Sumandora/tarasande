package su.mandora.tarasande_rejected_features

import net.fabricmc.api.ClientModInitializer
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventSuccessfulLoad
import su.mandora.tarasande.system.feature.commandsystem.ManagerCommand
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule
import su.mandora.tarasande.system.screen.informationsystem.ManagerInformation
import su.mandora.tarasande_rejected_features.command.CommandDeadByDaylightEscape
import su.mandora.tarasande_rejected_features.command.CommandSparkyHeal
import su.mandora.tarasande_rejected_features.information.*
import su.mandora.tarasande_rejected_features.module.ModuleAutoRescuePlatform
import su.mandora.tarasande_rejected_features.module.ModuleDropper
import su.mandora.tarasande_rejected_features.tarasandevalues.ClosedInventory

class TarasandeRejectedFeatures : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            ManagerModule.add(
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
