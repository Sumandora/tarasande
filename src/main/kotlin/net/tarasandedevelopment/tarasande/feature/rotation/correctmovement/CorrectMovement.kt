package net.tarasandedevelopment.tarasande.feature.rotation.correctmovement

import net.tarasandedevelopment.tarasande.feature.rotation.Rotations
import net.tarasandedevelopment.tarasande.feature.rotation.correctmovement.impl.Direct
import net.tarasandedevelopment.tarasande.feature.rotation.correctmovement.impl.PreventBackwardsSprinting
import net.tarasandedevelopment.tarasande.feature.rotation.correctmovement.impl.Silent

class CorrectMovement(rotations: Rotations) {

    init {
        PreventBackwardsSprinting(rotations)
        Direct(rotations)
        Silent(rotations)
    }

}