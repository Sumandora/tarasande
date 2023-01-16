package net.tarasandedevelopment.tarasande.feature.clientvalue.impl.debug.camera

import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues

class Camera {
    val viewModel = ValueButtonOwnerValues(this, "View model", ViewModel())

    val forceAspectRatio = ValueBoolean(this, "Force aspect ratio", false)
    val aspectRatio = object : ValueNumber(this, "Aspect ratio", 0.1, 1.0, 4.0, 0.1) {
        override fun isEnabled() = forceAspectRatio.value
    }

}