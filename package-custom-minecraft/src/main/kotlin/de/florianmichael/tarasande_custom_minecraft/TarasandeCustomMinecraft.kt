package de.florianmichael.tarasande_custom_minecraft

import de.florianmichael.tarasande_custom_minecraft.tarasandevalues.DesignValues
import net.fabricmc.api.ClientModInitializer
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.feature.tarasandevalue.TarasandeValues
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import su.mandora.event.EventDispatcher

class TarasandeCustomMinecraft : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            ValueButtonOwnerValues(TarasandeValues, "Design values", DesignValues)
        }
    }
}