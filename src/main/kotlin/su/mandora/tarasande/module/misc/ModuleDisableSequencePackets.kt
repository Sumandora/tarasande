package su.mandora.tarasande.module.misc

import de.florianmichael.viaprotocolhack.util.VersionList
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventIncrementSequence
import java.util.function.Consumer

class ModuleDisableSequencePackets : Module("Disable sequence packets", "Disables sequencing", ModuleCategory.MISC) {

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventIncrementSequence)
            event.cancelled = true
    }

    override fun isEnabled(): Boolean {
        return VersionList.isNewerOrEqualTo(VersionList.R1_19)
    }
}