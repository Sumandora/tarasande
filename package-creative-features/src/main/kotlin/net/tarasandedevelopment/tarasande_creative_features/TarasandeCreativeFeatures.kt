package net.tarasandedevelopment.tarasande_creative_features

import net.fabricmc.api.ClientModInitializer
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande_creative_features.creativesystem.ManagerCreative
import su.mandora.event.EventDispatcher

class TarasandeCreativeFeatures : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java, 1001) {
            ManagerCreative
        }
    }
}
