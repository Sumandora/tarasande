package net.tarasandedevelopment.tarasande.feature.rotation.correctmovement.impl

import net.tarasandedevelopment.tarasande.event.EventDispatcher
import net.tarasandedevelopment.tarasande.event.impl.EventJump
import net.tarasandedevelopment.tarasande.event.impl.EventVelocityYaw
import net.tarasandedevelopment.tarasande.feature.rotation.Rotations

class Direct(rotations: Rotations) {

    init {
        EventDispatcher.apply {
            add(EventJump::class.java) { event ->
                if (event.state != EventJump.State.PRE) return@add
                val fakeRotation = rotations.fakeRotation ?: return@add
                if (rotations.correctMovement.isSelected(2) || rotations.correctMovement.isSelected(3)) {
                    event.yaw = fakeRotation.yaw
                }
            }
            add(EventVelocityYaw::class.java) { event ->
                val fakeRotation = rotations.fakeRotation ?: return@add
                if (rotations.correctMovement.isSelected(2) || rotations.correctMovement.isSelected(3)) {
                    event.yaw = fakeRotation.yaw
                }
            }
        }
    }

}