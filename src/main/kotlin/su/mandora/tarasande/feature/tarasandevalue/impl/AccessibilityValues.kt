package su.mandora.tarasande.feature.tarasandevalue.impl

import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber

object AccessibilityValues {

    val doubleClickDelay = ValueNumber(this, "Double click delay", 0.0, 300.0, 1000.0, 100.0)
    val statusRenderTime = ValueNumber(this, "Status render time", 0.0, 2000.0, 10000.0, 500.0)

}
