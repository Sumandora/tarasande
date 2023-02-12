package net.tarasandedevelopment.tarasande.feature.tarasandevalue.impl

import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber

object AccessibilityValues {

    val doubleClickDelay = ValueNumber(this, "Double click delay", 0.0, 300.0, 1000.0, 100.0)
    val playSoundWhenClickingInSidebar = ValueBoolean(this, "Play sound when clicking in sidebar", true)

}