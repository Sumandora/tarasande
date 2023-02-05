package de.florianmichael.tarasande_creative_features

import de.florianmichael.tarasande_creative_features.clientvalue.CreativeValues
import net.fabricmc.api.ClientModInitializer
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import de.florianmichael.tarasande_creative_features.creativesystem.ManagerCreative
import net.tarasandedevelopment.tarasande.feature.clientvalue.impl.DebugValues
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import su.mandora.event.EventDispatcher

class TarasandeCreativeFeatures : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java, 1001) {
            ManagerCreative

            ValueButtonOwnerValues(DebugValues, "Creative", CreativeValues)
        }
    }
}
