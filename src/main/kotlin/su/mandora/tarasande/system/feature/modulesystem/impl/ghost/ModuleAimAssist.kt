package su.mandora.tarasande.system.feature.modulesystem.impl.ghost

import su.mandora.tarasande.event.impl.EventMouseDelta
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumberRange
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.DEFAULT_REACH
import su.mandora.tarasande.util.extension.kotlinruntime.prefer
import su.mandora.tarasande.util.extension.minecraft.isEntityHitResult
import su.mandora.tarasande.util.math.MathUtil
import su.mandora.tarasande.feature.rotation.api.Rotation
import su.mandora.tarasande.feature.rotation.api.RotationUtil
import su.mandora.tarasande.util.maxReach
import su.mandora.tarasande.util.player.PlayerUtil

class ModuleAimAssist : Module("Aim assist", "Helps you aim at enemies", ModuleCategory.GHOST) {

    private val fov = ValueNumber(this, "FOV", 0.0, Rotation.MAXIMUM_DELTA, Rotation.MAXIMUM_DELTA, 1.0)
    private val reach = ValueNumber(this, "Reach", 0.1, DEFAULT_REACH + 1.0, maxReach, 0.1)
    private val aimSpeed = ValueNumberRange(this, "Aim speed", 0.01, 0.02, 0.05, 0.1, 0.01)
    private val maxInfluence = ValueNumber(this, "Max influence", 0.0, 5.0, 20.0, 1.0)

    init {
        registerEvent(EventMouseDelta::class.java) { event ->
            val selfRotation = Rotation(mc.player ?: return@registerEvent)
            val entity = mc.world?.entities?.filter { PlayerUtil.isAttackable(it) }?.filter { mc.player?.distanceTo(it)!! < reach.value }?.filter { PlayerUtil.canVectorBeSeen(mc.player?.eyePos!!, it.eyePos) }?.minByOrNull { RotationUtil.getRotations(mc.player?.eyePos!!, it.eyePos).fov(selfRotation) } ?: return@registerEvent

            val boundingBox = entity.boundingBox.expand(entity.targetingMargin.toDouble())
            val bestAimPoint = MathUtil.getBestAimPoint(boundingBox)

            val rotation = RotationUtil.getRotations(mc.player?.eyePos!!, bestAimPoint)

            if (rotation.fov(selfRotation) > fov.value)
                return@registerEvent

            val smoothedRot = selfRotation.smoothedTurn(rotation, aimSpeed).correctSensitivity() // correct wrap

            val deltaRotation = smoothedRot.closestDelta(selfRotation)
            val cursorDeltas = Rotation.approximateCursorDeltas(deltaRotation).prefer { PlayerUtil.getTargetedEntity(DEFAULT_REACH, Rotation.calculateNewRotation(selfRotation, it), false)?.isEntityHitResult() == true }

            event.deltaX += cursorDeltas.first.coerceIn(-maxInfluence.value..maxInfluence.value)
            event.deltaY += cursorDeltas.second.coerceIn(-maxInfluence.value..maxInfluence.value)
        }
    }

}