package su.mandora.tarasande_protocol_spoofer

import net.fabricmc.api.ClientModInitializer
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventSuccessfulLoad
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues
import su.mandora.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.ProtocolSpooferValues
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.ResourcePackSpoofer

class TarasandeProtocolSpoofer : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            ResourcePackSpoofer

            ValueButtonOwnerValues(TarasandeValues, "Protocol spoofer values", ProtocolSpooferValues)
        }
    }
}
