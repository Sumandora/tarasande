package net.tarasandedevelopment.tarasande.feature.tarasandevalue.impl.debug.camera

import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues

object Camera {

    init {
        ValueButtonOwnerValues(this, "View model", ViewModel)
    }

    val forceAspectRatio = ValueBoolean(this, "Force aspect ratio", false)
    val aspectRatio = ValueNumber(this, "Aspect ratio", 0.1, 1.0, 4.0, 0.1, isEnabled = { forceAspectRatio.value })
}
