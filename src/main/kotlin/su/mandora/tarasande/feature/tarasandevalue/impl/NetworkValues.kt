package su.mandora.tarasande.feature.tarasandevalue.impl

import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode

object NetworkValues {

    val allowAddressParsingForBlacklistedServers = ValueBoolean(this, "Allow address parsing for blacklisted servers", true)
    val removeNettyExceptionHandling = ValueMode(this, "Remove Netty exception handling", true, "Timeout", "Wrong packets")
    val printExceptionStacktrace = ValueBoolean(this, "Print exception stacktrace", false)

}