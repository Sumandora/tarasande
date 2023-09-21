package su.mandora.tarasande.feature.rotation.component

import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.feature.rotation.api.Rotation
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumberRange

class RotateToOrigin(val rotations: Rotations) {

    private val rotateToOriginSpeed = ValueNumberRange(rotations, "Rotate to origin speed", 0.0, 1.0, 1.0, 1.0, 0.1)

    fun handleRotateToOrigin(realRotation: Rotation) {
        val rotation = rotations.fakeRotation!!
            .smoothedTurn(realRotation, rotateToOriginSpeed)
            .correctSensitivity()
        if (rotations.fakeRotation == rotation) {
            val actualRotation = realRotation.correctSensitivity()
            rotations.fakeRotation = null

            mc.player?.apply {
                actualRotation.applyOn(this)
                // Prevent rotation interpolation
                renderYaw = actualRotation.yaw
                lastRenderYaw = actualRotation.yaw
                prevYaw = actualRotation.yaw
            }
        } else {
            rotations.fakeRotation = rotation
        }
    }

}