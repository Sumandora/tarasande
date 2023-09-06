package su.mandora.tarasande.feature.rotation.components.correctmovement.impl

import net.minecraft.util.math.MathHelper
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventIsWalkingForward
import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.util.player.PlayerUtil
import kotlin.math.abs

class PreventBackwardsSprinting(rotations: Rotations, isEnabled: () -> Boolean) {

    init {
        EventDispatcher.add(EventIsWalkingForward::class.java) {
            val fakeRotation = rotations.fakeRotation ?: return@add
            if (isEnabled())
                if (PlayerUtil.isPlayerMoving())
                    it.walksForward = abs(MathHelper.wrapDegrees(fakeRotation.yaw - PlayerUtil.getMoveDirection())) <= 45F
        }
    }

}