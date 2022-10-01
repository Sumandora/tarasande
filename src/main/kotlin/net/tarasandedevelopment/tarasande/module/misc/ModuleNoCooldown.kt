package net.tarasandedevelopment.tarasande.module.misc

import de.florianmichael.viaprotocolhack.util.VersionList
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.event.Priority
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventItemCooldown
import java.util.function.Consumer

class ModuleNoCooldown : Module("No cooldown", "Removes any cooldown from items", ModuleCategory.MISC) {

    @Priority(999) // is being overwritten by NoSlowdown
    val eventConsumer = Consumer<Event> { event ->
        if (event is EventItemCooldown) {
            event.cooldown = 0.0f
        }
    }

    override fun isEnabled(): Boolean {
        return VersionList.isNewerTo(VersionList.R1_8)
    }
}