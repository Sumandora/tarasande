package de.florianmichael.tarasande_custom_minecraft

import de.florianmichael.tarasande_custom_minecraft.tarasandevalues.DesignValues
import de.florianmichael.tarasande_custom_minecraft.tarasandevalues.debug.DetailedConnectionStatus
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.tarasandedevelopment.tarasande.TARASANDE_NAME
import net.tarasandedevelopment.tarasande.event.impl.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.feature.tarasandevalue.TarasandeValues
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import net.tarasandedevelopment.tarasande.event.EventDispatcher

class TarasandeCustomMinecraft : ClientModInitializer {

    companion object {
        val tarasandeProtocolHackLoaded = FabricLoader.getInstance().isModLoaded("$TARASANDE_NAME-protocol-hack")
    }

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {

            ValueButtonOwnerValues(TarasandeValues, "Design values", DesignValues)

            DetailedConnectionStatus
        }
    }
}