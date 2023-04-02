package su.mandora.tarasande.feature.rotation.correctmovement.impl

import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventJump
import su.mandora.tarasande.event.impl.EventVelocityYaw
import su.mandora.tarasande.feature.rotation.Rotations

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