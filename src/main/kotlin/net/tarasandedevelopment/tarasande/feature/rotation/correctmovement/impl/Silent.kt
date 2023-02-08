package net.tarasandedevelopment.tarasande.feature.rotation.correctmovement.impl

import net.tarasandedevelopment.tarasande.event.EventInput
import net.tarasandedevelopment.tarasande.feature.rotation.Rotations
import net.tarasandedevelopment.tarasande.mc
import su.mandora.event.EventDispatcher
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt

class Silent(rotations: Rotations) {

    init {
        EventDispatcher.add(EventInput::class.java) { event ->
            val fakeRotation = rotations.fakeRotation ?: return@add
            if (event.input != mc.player?.input)
                return@add

            if(!rotations.correctMovement.isSelected(3))
                return@add

            if (event.movementForward == 0.0F && event.movementSideways == 0.0F)
                return@add

            val realYaw = mc.player!!.yaw
            val fakeYaw = fakeRotation.yaw

            val moveX = event.movementSideways * cos(Math.toRadians(realYaw.toDouble())) - event.movementForward * sin(Math.toRadians(realYaw.toDouble()))
            val moveZ = event.movementForward * cos(Math.toRadians(realYaw.toDouble())) + event.movementSideways * sin(Math.toRadians(realYaw.toDouble()))

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
                event.movementForward = round(bestMovement[1]).toInt().toFloat()
                event.movementSideways = round(bestMovement[2]).toInt().toFloat()
            }
        }
    }
}