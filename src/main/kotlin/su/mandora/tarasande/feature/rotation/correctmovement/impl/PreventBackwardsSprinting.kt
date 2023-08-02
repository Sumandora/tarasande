package su.mandora.tarasande.feature.rotation.correctmovement.impl

import net.minecraft.util.math.MathHelper
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventIsWalkingForward
import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.util.player.PlayerUtil
import kotlin.math.abs

class PreventBackwardsSprinting(rotations: Rotations) {

    init {
        EventDispatcher.add(EventIsWalkingForward::class.java) {
            val fakeRotation = rotations.fakeRotation ?: return@add
            if (rotations.correctMovement.isSelected(1))
                if (PlayerUtil.isPlayerMoving())
                    it.walksForward = abs(MathHelper.wrapDegrees(fakeRotation.yaw - PlayerUtil.getMoveDirection())) <= 45F
        }
    }

}