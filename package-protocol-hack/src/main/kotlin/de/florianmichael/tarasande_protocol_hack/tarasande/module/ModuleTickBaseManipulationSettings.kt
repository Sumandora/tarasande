package de.florianmichael.tarasande_protocol_hack.tarasande.module

import de.florianmichael.tarasande_protocol_hack.tarasande.event.EventSkipIdlePacket
import de.florianmichael.tarasande_protocol_hack.tarasande.values.ProtocolHackValues
import de.florianmichael.tarasande_protocol_hack.tarasande.values.formatRange
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.exploit.ModuleTickBaseManipulation

fun modifyModuleTickBaseManipulation() {
    ManagerModule.get(ModuleTickBaseManipulation::class.java).apply {
        val chargeOnIdlePacketSkip = ValueBoolean(this, "Charge on idle packet skip (" + formatRange(*ProtocolHackValues.sendIdlePacket.version[0].inverse()) + ")", false, isEnabled = { !ProtocolHackValues.sendIdlePacket.value })

        registerEvent(EventSkipIdlePacket::class.java) {
            if (chargeOnIdlePacketSkip.isEnabled() && chargeOnIdlePacketSkip.value)
                shifted += mc.renderTickCounter.tickTime.toLong()
        }
    }
}
