package su.mandora.tarasande.feature.rotation.correctmovement.impl

import net.minecraft.util.math.MathHelper
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventCanSprint
import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.mc
import kotlin.math.abs

class PreventBackwardsSprinting(rotations: Rotations) {

    init {
        EventDispatcher.add(EventCanSprint::class.java) {
            val fakeRotation = rotations.fakeRotation ?: return@add
            if (rotations.correctMovement.isSelected(1)) {
                if (abs(MathHelper.wrapDegrees(fakeRotation.yaw - mc.player?.yaw!!)) > 45F)
                    it.canSprint = false // oof
            }
        }
    }

}