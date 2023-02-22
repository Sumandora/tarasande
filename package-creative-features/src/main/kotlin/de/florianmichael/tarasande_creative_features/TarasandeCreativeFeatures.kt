package de.florianmichael.tarasande_creative_features

import de.florianmichael.tarasande_creative_features.command.CommandCheckCMDBlock
import de.florianmichael.tarasande_creative_features.creativesystem.ManagerCreative
import de.florianmichael.tarasande_creative_features.tarasandevalue.CreativeValues
import net.fabricmc.api.ClientModInitializer
import net.tarasandedevelopment.tarasande.event.impl.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.feature.tarasandevalue.impl.DebugValues
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import net.tarasandedevelopment.tarasande.system.feature.commandsystem.ManagerCommand
import net.tarasandedevelopment.tarasande.event.EventDispatcher

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
