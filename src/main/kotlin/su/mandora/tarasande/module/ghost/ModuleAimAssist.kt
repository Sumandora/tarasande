package su.mandora.tarasande.module.ghost

import net.minecraft.util.math.MathHelper
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventMouseDelta
import su.mandora.tarasande.module.combat.ModuleKillAura
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.math.rotation.RotationUtil
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.value.ValueNumber
import su.mandora.tarasande.value.ValueNumberRange
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
            val bestAimPoint = TarasandeMain.get().managerModule.get(ModuleKillAura::class.java).getBestAimPoint(boundingBox)

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