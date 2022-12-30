package net.tarasandedevelopment.tarasande.feature.clientvalue.impl

import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean

object PrivacyValues {

    val disableTelemetry = ValueBoolean(this, "Disable telemetry", true)
    val disableRealmsRequests = ValueBoolean(this, "Disable realms requests", true)

}