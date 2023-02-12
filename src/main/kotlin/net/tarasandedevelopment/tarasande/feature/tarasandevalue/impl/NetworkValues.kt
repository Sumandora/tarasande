package net.tarasandedevelopment.tarasande.feature.tarasandevalue.impl

import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode

object NetworkValues {

    val allowAddressParsingForBlacklistedServers = ValueBoolean(this, "Allow address parsing for blacklisted servers", true)
    val removeNettyExceptionHandling = ValueMode(this, "Remove Netty exception handling", true, "Timeout", "Wrong packets")
}