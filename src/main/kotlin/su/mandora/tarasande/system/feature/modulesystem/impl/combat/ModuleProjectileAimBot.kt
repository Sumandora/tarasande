package su.mandora.tarasande.system.feature.modulesystem.impl.combat

import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ArrowItem
import net.minecraft.item.BowItem
import net.minecraft.item.CrossbowItem
import net.minecraft.item.ItemStack
import net.minecraft.util.math.Box
import su.mandora.tarasande.event.impl.EventRender3D
import su.mandora.tarasande.event.impl.EventRotation
import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueColor
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumberRange
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.system.feature.modulesystem.impl.player.ModuleFastUse
import su.mandora.tarasande.util.extension.minecraft.minus
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.math.rotation.RotationUtil
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.player.prediction.PredictionEngine
import su.mandora.tarasande.util.player.prediction.projectile.ProjectileUtil
import su.mandora.tarasande.util.render.RenderUtil
import kotlin.math.atan2
import kotlin.math.sqrt

class ModuleProjectileAimBot : Module("Projectile aim bot", "Automatically aims at targets with (cross-)bow", ModuleCategory.COMBAT) {

    private val aimSpeed = ValueNumberRange(this, "Aim speed", 0.1, 1.0, 1.0, 1.0, 0.1)
    private val maxPrediction = ValueNumber(this, "Max prediction", 0.0, 40.0, 100.0, 1.0)
    private val throughWalls = ValueBoolean(this, "Through walls", false)
    private val predictionColor = ValueColor(this, "Prediction color", 0.0, 1.0, 1.0, 1.0)

    private val gravity = 0.006

    private var predictedBox: Box? = null

    private fun calcVelocity(stack: ItemStack): Double {
        return BowItem.getPullProgress(if (mc.player?.isUsingItem!! && !ManagerModule.get(ModuleFastUse::class.java).enabled.value) mc.player?.itemUseTime!! else stack.maxUseTime).toDouble()
    }

    // https://en.wikipedia.org/wiki/Projectile_motion#Angle_%CE%B8_required_to_hit_coordinate_(x,_y)
    private fun calcPitch(stack: ItemStack, dist: Double, deltaY: Double): Double {
        val velocity = calcVelocity(stack)
        val root = sqrt(velocity * velocity * velocity * velocity - gravity * (gravity * dist * dist + 2 * deltaY * velocity * velocity))
        // Use the negated one first, because it's usually better
        return -Math.toDegrees(atan2(velocity * velocity /*+/-*/ - root, gravity * dist))
    }

    private fun deadReckoning(stack: ItemStack, entity: Entity, rotation: Rotation): Box {
        if (entity !is PlayerEntity)
            return entity.boundingBox
        val predicted = ProjectileUtil.predict(stack, rotation, false)
        if (predicted.size <= 0) return entity.boundingBox

        return PredictionEngine.predictState((2 + predicted.size).coerceAtMost(maxPrediction.value.toInt()), entity).first.boundingBox
    }

    init {
        registerEvent(EventRotation::class.java) { event ->
            predictedBox = null
            val stack = mc.player?.getStackInHand(mc.player?.activeHand) ?: return@registerEvent
            if ((stack.item !is BowItem || !mc.player?.isUsingItem!!) && !(stack.item is CrossbowItem && CrossbowItem.getProjectiles(stack).any { it.item is ArrowItem })) return@registerEvent

            val entity = mc.world?.entities?.filter { PlayerUtil.isAttackable(it) && (throughWalls.value || PlayerUtil.canVectorBeSeen(mc.player?.eyePos!!, it.eyePos)) }?.minByOrNull { RotationUtil.getRotations(mc.player?.eyePos!!, it.eyePos).fov(Rotation(mc.player!!)) } ?: return@registerEvent

            var target = entity.boundingBox.center

            var solution = calcPitch(stack, (mc.player?.eyePos!! - target).horizontalLength(), target.y - mc.player?.eyeY!!)

            if (solution.isNaN()) return@registerEvent

            var yaw = RotationUtil.getYaw(target - mc.player?.eyePos!!)
            var rotation = Rotation(yaw.toFloat(), solution.toFloat())

            // DEAD RECKONING
            val box = deadReckoning(stack, entity, rotation)
            target = box.center

            solution = calcPitch(stack, (mc.player?.eyePos!! - target).horizontalLength(), target.y - mc.player?.eyeY!!)

            if (solution.isNaN()) return@registerEvent

            yaw = RotationUtil.getYaw(target - mc.player?.eyePos!!)
            rotation = Rotation(yaw.toFloat(), solution.toFloat())
            // DEAD RECKONING

            val currentRot = Rotations.fakeRotation ?: Rotation(mc.player!!)
            val smoothedRot = currentRot.smoothedTurn(rotation, aimSpeed)

            predictedBox = box
            event.rotation = smoothedRot.correctSensitivity()
        }

        registerEvent(EventRender3D::class.java) { event ->
            if (predictedBox != null)
                RenderUtil.blockOutline(event.matrices, predictedBox!!, predictionColor.getColor().rgb)
        }
    }
}