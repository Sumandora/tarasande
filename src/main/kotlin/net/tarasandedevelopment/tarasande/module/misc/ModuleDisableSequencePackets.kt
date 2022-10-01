package net.tarasandedevelopment.tarasande.module.misc

import de.florianmichael.viaprotocolhack.util.VersionList
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventIncrementSequence
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