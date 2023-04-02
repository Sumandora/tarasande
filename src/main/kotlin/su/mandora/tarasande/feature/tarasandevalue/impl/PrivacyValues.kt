package su.mandora.tarasande.feature.tarasandevalue.impl

import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean

object PrivacyValues {

    val disableTelemetry = ValueBoolean(this, "Disable telemetry", true)
    val disableRealmsRequests = ValueBoolean(this, "Disable realms requests", true)

}