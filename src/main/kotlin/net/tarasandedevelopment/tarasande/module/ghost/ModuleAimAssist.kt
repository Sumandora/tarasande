package net.tarasandedevelopment.tarasande.module.ghost

import net.minecraft.util.math.MathHelper
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventMouseDelta
import net.tarasandedevelopment.tarasande.util.math.MathUtil
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.value.ValueNumber
import net.tarasandedevelopment.tarasande.value.ValueNumberRange
import java.util.function.Consumer

class ModuleAimAssist : Module("Aim assist", "Helps you aim at enemies", ModuleCategory.GHOST) {

    private val fov = ValueNumber(this, "FOV", 0.0, 255.0, 255.0, 1.0)
    private val reach = ValueNumber(this, "Reach", 0.0, 4.0, 6.0, 0.1)
    private val aimSpeed = ValueNumberRange(this, "Aim speed", 0.01, 0.02, 0.05, 0.1, 0.01)
    private val maxInfluence = ValueNumber(this, "Max influence", 0.0, 5.0, 20.0, 1.0)

    val eventConsumer = Consumer<Event> { event ->
        if(event is EventMouseDelta) {
            val selfRotation = Rotation(mc.player ?: return@Consumer)
            val entity = mc.world?.entities?.filter { PlayerUtil.isAttackable(it) }?.filter { mc.player?.distanceTo(it)!! < reach.value }?.filter { PlayerUtil.canVectorBeSeen(mc.player?.eyePos!!, it.eyePos) }?.minByOrNull { RotationUtil.getRotations(mc.player?.eyePos!!, it.eyePos).fov(selfRotation) } ?: return@Consumer

            val boundingBox = entity.boundingBox.expand(entity.targetingMargin.toDouble())
            val bestAimPoint = MathUtil.getBestAimPoint(boundingBox)

            val rotation = RotationUtil.getRotations(mc.player?.eyePos!!, bestAimPoint)

            if (rotation.fov(selfRotation) > fov.value)
                return@Consumer

            val smoothedRot = Rotation(selfRotation).smoothedTurn(rotation, aimSpeed).correctSensitivity() // correct wrap

            val deltaRotation = smoothedRot.calcDelta(selfRotation)
            val cursorDeltas = Rotation.approximateCursorDeltas(deltaRotation)

            event.deltaX += MathHelper.clamp(cursorDeltas.first, -maxInfluence.value, maxInfluence.value)
            event.deltaY += MathHelper.clamp(cursorDeltas.second, -maxInfluence.value, maxInfluence.value)
        }
    }

}