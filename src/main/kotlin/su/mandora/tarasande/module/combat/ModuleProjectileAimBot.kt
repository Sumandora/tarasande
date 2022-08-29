package su.mandora.tarasande.module.combat

import net.minecraft.entity.LivingEntity
import net.minecraft.item.ArrowItem
import net.minecraft.item.BowItem
import net.minecraft.item.CrossbowItem
import net.minecraft.item.ItemStack
import net.minecraft.util.math.MathHelper
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventPollEvents
import su.mandora.tarasande.mixin.accessor.ICrossbowItem
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.math.rotation.RotationUtil
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueNumberRange
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer
import kotlin.math.atan2
import kotlin.math.sqrt

class ModuleProjectileAimBot : Module("Projectile aim bot", "Automatically aims at targets with (cross-)bow", ModuleCategory.COMBAT) {

    private val aimSpeed = ValueNumberRange(this, "Aim speed", 0.1, 1.0, 1.0, 1.0, 0.1)
    private val lockView = ValueBoolean(this, "Lock view", false)

    private val gravity = 0.006

    private fun calcVelocity(stack: ItemStack): Double {
        return BowItem.getPullProgress(if (mc.player?.isUsingItem!!) mc.player?.itemUseTime!! else stack.maxUseTime).toDouble()
    }

    // https://en.wikipedia.org/wiki/Projectile_motion#Angle_%CE%B8_required_to_hit_coordinate_(x,_y)
    private fun calcPitch(stack: ItemStack, dist: Double, deltaY: Double): Double {
        val velocity = calcVelocity(stack)
        val root = sqrt(velocity * velocity * velocity * velocity - gravity * (gravity * dist * dist + 2 * deltaY * velocity * velocity))
        // Use the negated one first, because it's usually better
        return -Math.toDegrees(atan2(velocity * velocity - root, gravity * dist))
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventPollEvents -> {
                if (!mc.player?.isUsingItem!!) return@Consumer
                val stack = mc.player?.getStackInHand(mc.player?.activeHand) ?: return@Consumer
                if (!(stack.item is BowItem || (stack.item is CrossbowItem && (stack.item as ICrossbowItem).tarasande_invokeGetProjectiles(stack).any { it.item is ArrowItem }))) return@Consumer

                for (entity in mc.world?.entities?.filter { PlayerUtil.isAttackable(it) }?.map { it as LivingEntity }?.sortedBy { RotationUtil.getRotations(mc.player?.eyePos!!, it.eyePos).fov(Rotation(mc.player!!)) }?.sortedBy { !PlayerUtil.canVectorBeSeen(mc.player?.eyePos!!, it.boundingBox.center) } ?: return@Consumer) {
                    val target = entity.boundingBox.offset(mc.player?.velocity?.negate()).center

                    val solution = calcPitch(stack, mc.player?.eyePos?.subtract(target)?.horizontalLength()!!, target.y - mc.player?.eyeY!!)

                    if (solution.isNaN()) continue

                    val yaw = RotationUtil.getYaw(target.subtract(mc.player?.eyePos))
                    val rotation = Rotation(yaw.toFloat(), solution.toFloat())

                    val currentRot = if (RotationUtil.fakeRotation != null) Rotation(RotationUtil.fakeRotation!!) else Rotation(mc.player!!)
                    val smoothedRot = currentRot.smoothedTurn(rotation, if (aimSpeed.minValue == 1.0 && aimSpeed.maxValue == 1.0) 1.0
                    else MathHelper.clamp((if (aimSpeed.minValue == aimSpeed.maxValue) aimSpeed.minValue
                    else ThreadLocalRandom.current().nextDouble(aimSpeed.minValue, aimSpeed.maxValue)) * RenderUtil.deltaTime * 0.05, 0.0, 1.0))

                    event.rotation = smoothedRot

                    if (lockView.value) {
                        mc.player?.yaw = event.rotation.yaw
                        mc.player?.pitch = event.rotation.pitch
                    }

                    event.minRotateToOriginSpeed = aimSpeed.minValue
                    event.maxRotateToOriginSpeed = aimSpeed.maxValue
                    return@Consumer
                }
            }
        }
    }

}