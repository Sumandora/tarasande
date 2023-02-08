package net.tarasandedevelopment.tarasande.feature.rotation.correctmovement.impl

import net.minecraft.util.math.MathHelper
import net.tarasandedevelopment.tarasande.event.EventCanSprint
import net.tarasandedevelopment.tarasande.feature.rotation.Rotations
import net.tarasandedevelopment.tarasande.mc
import su.mandora.event.EventDispatcher
import kotlin.math.abs

class PreventBackwardsSprinting(rotations: Rotations) {

    init {
        EventDispatcher.add(EventCanSprint::class.java) {
            val fakeRotation = rotations.fakeRotation ?: return@add
            if (rotations.correctMovement.isSelected(1)) {
                if (abs(MathHelper.wrapDegrees(fakeRotation.yaw - mc.player?.yaw!!)) > 45.0F)
                    it.canSprint = false // oof
            }
        }
    }

}