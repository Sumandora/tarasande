package su.mandora.tarasande_custom_minecraft

import su.mandora.tarasande_custom_minecraft.tarasandevalues.DesignValues
import su.mandora.tarasande_custom_minecraft.tarasandevalues.debug.DetailedConnectionStatus
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventSuccessfulLoad
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues
import su.mandora.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues

class TarasandeCustomMinecraft : ClientModInitializer {

    companion object {
        val viaFabricPlusLoaded = FabricLoader.getInstance().isModLoaded("viafabricplus")
    }

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {

            ValueButtonOwnerValues(TarasandeValues, "Design values", DesignValues)

            DetailedConnectionStatus
        }
    }
}
