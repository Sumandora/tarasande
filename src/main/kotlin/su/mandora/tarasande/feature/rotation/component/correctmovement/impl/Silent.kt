package su.mandora.tarasande.feature.rotation.component.correctmovement.impl

import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventInput
import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.mc
import su.mandora.tarasande.util.extension.minecraft.setMovementForward
import su.mandora.tarasande.util.extension.minecraft.setMovementSideways
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt

class Silent(rotations: Rotations, isEnabled: () -> Boolean) {

    init {
        EventDispatcher.add(EventInput::class.java, 1) { event ->
            val fakeRotation = rotations.fakeRotation ?: return@add
            if (event.input != mc.player?.input)
                return@add

            if (!isEnabled())
                return@add

            if (event.input.movementForward == 0F && event.input.movementSideways == 0F)
                return@add

            val realYaw = mc.player!!.yaw
            val fakeYaw = fakeRotation.yaw

            val moveX = event.input.movementSideways * cos(Math.toRadians(realYaw.toDouble())) - event.input.movementForward * sin(Math.toRadians(realYaw.toDouble()))
            val moveZ = event.input.movementForward * cos(Math.toRadians(realYaw.toDouble())) + event.input.movementSideways * sin(Math.toRadians(realYaw.toDouble()))

            var bestMovement: DoubleArray? = null

            for (forward in -1..1) for (strafe in -1..1) {
                val newMoveX = strafe * cos(Math.toRadians(fakeYaw.toDouble())) - forward * sin(Math.toRadians(fakeYaw.toDouble()))
                val newMoveZ = forward * cos(Math.toRadians(fakeYaw.toDouble())) + strafe * sin(Math.toRadians(fakeYaw.toDouble()))

                val deltaX = newMoveX - moveX
                val deltaZ = newMoveZ - moveZ

                val dist = sqrt(deltaX * deltaX + deltaZ * deltaZ)

                if (bestMovement == null || bestMovement[0] > dist) {
                    bestMovement = doubleArrayOf(dist, forward.toDouble(), strafe.toDouble())
                }
            }

            if (bestMovement != null) {
                event.input.setMovementForward(round(bestMovement[1].toFloat()))
                event.input.setMovementSideways(round(bestMovement[2].toFloat()))
            }
        }
    }
}