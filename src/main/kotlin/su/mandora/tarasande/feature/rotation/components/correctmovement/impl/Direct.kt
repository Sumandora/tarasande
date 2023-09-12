package su.mandora.tarasande.feature.rotation.components.correctmovement.impl

import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventJump
import su.mandora.tarasande.event.impl.EventVelocityYaw
import su.mandora.tarasande.feature.rotation.Rotations

class Direct(rotations: Rotations, isEnabled: () -> Boolean) {

    init {
        EventDispatcher.apply {
            add(EventJump::class.java, 1) { event ->
                if (event.state != EventJump.State.PRE) return@add
                val fakeRotation = rotations.fakeRotation ?: return@add
                if (isEnabled())
                    event.yaw = fakeRotation.yaw
            }
            add(EventVelocityYaw::class.java, 1) { event ->
                val fakeRotation = rotations.fakeRotation ?: return@add
                if (isEnabled())
                    event.yaw = fakeRotation.yaw
            }
        }
    }

}