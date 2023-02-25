package de.florianmichael.tarasande_custom_minecraft.tarasandevalues

import de.florianmichael.tarasande_custom_minecraft.tarasandevalues.optimization.OptimizationValues
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues

object DesignValues {

    init {
        ValueButtonOwnerValues(this, "Optimization values", OptimizationValues)
    }

    val smoothScrolling = ValueBoolean(this, "Smooth scrolling", false)
}
