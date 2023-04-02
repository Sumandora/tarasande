package su.mandora.tarasande_custom_minecraft.tarasandevalues

import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import su.mandora.tarasande_custom_minecraft.tarasandevalues.optimization.OptimizationValues

object DesignValues {

    init {
        ValueButtonOwnerValues(this, "Optimization values", OptimizationValues)
    }

    val smoothScrolling = ValueBoolean(this, "Smooth scrolling", false)
}
