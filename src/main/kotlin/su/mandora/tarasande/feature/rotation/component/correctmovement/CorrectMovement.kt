package su.mandora.tarasande.feature.rotation.component.correctmovement

import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.feature.rotation.component.correctmovement.impl.Direct
import su.mandora.tarasande.feature.rotation.component.correctmovement.impl.PreventBackwardsSprinting
import su.mandora.tarasande.feature.rotation.component.correctmovement.impl.Silent
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode

class CorrectMovement(rotations: Rotations) {

    private val value = ValueMode(rotations, "Correct movement", false, "Off", "Prevent Backwards Sprinting", "Direct", "Silent")

    init {
        PreventBackwardsSprinting(rotations) { value.isSelected(1) }
        Direct(rotations) { value.isSelected(2) || value.isSelected(3) }
        Silent(rotations) { value.isSelected(3) }
    }

    fun allowsBackwards() = !value.isSelected(1)

}