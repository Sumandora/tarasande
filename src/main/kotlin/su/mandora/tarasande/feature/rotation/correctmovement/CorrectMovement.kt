package su.mandora.tarasande.feature.rotation.correctmovement

import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.feature.rotation.correctmovement.impl.Direct
import su.mandora.tarasande.feature.rotation.correctmovement.impl.PreventBackwardsSprinting
import su.mandora.tarasande.feature.rotation.correctmovement.impl.Silent

class CorrectMovement(rotations: Rotations) {

    init {
        PreventBackwardsSprinting(rotations)
        Direct(rotations)
        Silent(rotations)
    }

}