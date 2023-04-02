package su.mandora.tarasande_creative_features

import net.fabricmc.api.ClientModInitializer
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventSuccessfulLoad
import su.mandora.tarasande.feature.tarasandevalue.impl.DebugValues
import su.mandora.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import su.mandora.tarasande.system.feature.commandsystem.ManagerCommand
import su.mandora.tarasande_creative_features.command.CommandCheckCMDBlock
import su.mandora.tarasande_creative_features.creativesystem.ManagerCreative
import su.mandora.tarasande_creative_features.tarasandevalues.CreativeValues

class TarasandeCreativeFeatures : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java, 1001) {
            ManagerCreative
            ValueButtonOwnerValues(DebugValues, "Creative", CreativeValues)

            ManagerCommand.add(
                CommandCheckCMDBlock()
            )
        }
    }
}
