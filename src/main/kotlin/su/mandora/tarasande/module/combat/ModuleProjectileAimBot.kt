package su.mandora.tarasande.module.combat

import net.minecraft.entity.Entity
import net.minecraft.item.ArrowItem
import net.minecraft.item.BowItem
import net.minecraft.item.CrossbowItem
import net.minecraft.item.ItemStack
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventPollEvents
import net.tarasandedevelopment.tarasande.mixin.accessor.ICrossbowItem
import su.mandora.tarasande.module.player.ModuleFastUse
import su.mandora.tarasande.util.extension.minus
import su.mandora.tarasande.util.extension.plus
import su.mandora.tarasande.util.extension.times
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.math.rotation.RotationUtil
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.player.ProjectileUtil
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueNumber
import su.mandora.tarasande.value.ValueNumberRange
import java.util.function.Consumer
import kotlin.math.atan2
import kotlin.math.sqrt

class ModuleProjectileAimBot : Module("Projectile aim bot", "Automatically aims at targets with (cross-)bow", ModuleCategory.COMBAT) {

    private val aimSpeed = ValueNumberRange(this, "Aim speed", 0.1, 1.0, 1.0, 1.0, 0.1)
    private val lockView = ValueBoolean(this, "Lock view", false)
    private val predictionAmount = ValueNumber(this, "Prediction amount", 0.0, 1.0, 2.0, 0.1)

    private val gravity = 0.006

    private fun calcVelocity(stack: ItemStack): Double {
        return BowItem.getPullProgress(if (mc.player?.isUsingItem!! && !TarasandeMain.get().managerModule.get(ModuleFastUse::class.java).enabled) mc.player?.itemUseTime!! else stack.maxUseTime).toDouble()
    }

    // https://en.wikipedia.org/wiki/Projectile_motion#Angle_%CE%B8_required_to_hit_coordinate_(x,_y)
    private fun calcPitch(stack: ItemStack, dist: Double, deltaY: Double): Double {
        val velocity = calcVelocity(stack)
        val root = sqrt(velocity * velocity * velocity * velocity - gravity * (gravity * dist * dist + 2 * deltaY * velocity * velocity))
        // Use the negated one first, because it's usually better
        return -Math.toDegrees(atan2(velocity * velocity /*+/-*/- root, gravity * dist))
    }

    private fun deadReckoning(stack: ItemStack, entity: Entity, rotation: Rotation): Vec3d {
        val predicted = ProjectileUtil.predict(stack, rotation, false)
        if(predicted.size <= 0) return entity.boundingBox.center
        val prev = Vec3d(entity.prevX, entity.prevY, entity.prevZ)
        return entity.boundingBox.center + (entity.pos!! - prev).withAxis(Direction.Axis.Y, 0.0) * (predicted.size.toDouble() * predictionAmount.value)
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventPollEvents -> {
                if (!mc.player?.isUsingItem!!) return@Consumer
                val stack = mc.player?.getStackInHand(mc.player?.activeHand) ?: return@Consumer
                if (stack.item !is BowItem && !(stack.item is CrossbowItem && (stack.item as ICrossbowItem).tarasande_invokeGetProjectiles(stack).any { it.item is ArrowItem })) return@Consumer

                val entity = mc.world?.entities?.filter { PlayerUtil.isAttackable(it) && PlayerUtil.canVectorBeSeen(mc.player?.eyePos!!, it.eyePos) }?.minByOrNull { RotationUtil.getRotations(mc.player?.eyePos!!, it.eyePos).fov(Rotation(mc.player!!)) } ?: return@Consumer

                var target = entity.boundingBox.center

                var solution = calcPitch(stack, mc.player?.eyePos?.distanceTo(target)!!, target.y - mc.player?.eyeY!!)

                if (solution.isNaN()) return@Consumer

                var yaw = RotationUtil.getYaw(target - mc.player?.eyePos!!)
                var rotation = Rotation(yaw.toFloat(), solution.toFloat())

                // DEAD RECKONING
                target = deadReckoning(stack, entity, rotation)

                solution = calcPitch(stack, mc.player?.eyePos?.distanceTo(target)!!, target.y - mc.player?.eyeY!!)

                if (solution.isNaN()) return@Consumer

                yaw = RotationUtil.getYaw(target - mc.player?.eyePos!!)
                rotation = Rotation(yaw.toFloat(), solution.toFloat())
                // DEAD RECKONING

                val currentRot = if (RotationUtil.fakeRotation != null) Rotation(RotationUtil.fakeRotation!!) else Rotation(mc.player!!)
                val smoothedRot = currentRot.smoothedTurn(rotation, aimSpeed)

                event.rotation = smoothedRot.correctSensitivity()

                if (lockView.value) {
                    mc.player?.yaw = event.rotation.yaw
                    mc.player?.pitch = event.rotation.pitch
                }

                event.minRotateToOriginSpeed = aimSpeed.minValue
                event.maxRotateToOriginSpeed = aimSpeed.maxValue
            }
        }
    }

}