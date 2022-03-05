package su.mandora.tarasande.module.movement

import net.minecraft.entity.effect.StatusEffects
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventKeyBindingIsPressed
import su.mandora.tarasande.event.EventMovement
import su.mandora.tarasande.mixin.accessor.IVec3d
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueMode
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin


class ModuleSpeed : Module("Speed", "Makes you move faster", ModuleCategory.MOVEMENT) {

    private val jumpHeight = ValueNumber(this, "Jump height", 0.0, 1.0, 2.0, 0.1)
    private val lowHop = object : ValueBoolean(this, "Low hop", false) {
        override fun isVisible() = jumpHeight.value > 0.0
    }
    private val gravity = ValueNumber(this, "Gravity", 0.0, 1.0, 2.0, 0.1)
    private val gainMethod = ValueMode(this, "Gain method", true, "Jump", "Pulse")
    private val pulseDelay = object : ValueNumber(this, "Pulse delay", 0.0, 200.0, 1000.0, 1.0) {
        override fun isVisible() = gainMethod.isSelected(1)
    }
    private val speedValue = object : ValueNumber(this, "Speed", 0.0, 0.28, 1.0, 0.01) {
        override fun isVisible() = gainMethod.anySelected()
    }
    private val speedDivider = ValueNumber(this, "Speed divider", 1.0, 60.0, 200.0, 1.0)
    private val turnRate = ValueNumber(this, "Turn rate", 0.0, 180.0, 180.0, 1.0)

    private val timeUtil = TimeUtil()
    private var speed = 0.0
    private var moveDir = 0.0
    private var firstMove = true

    override fun onEnable() {
        firstMove = true
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventMovement -> {
                if (event.entity != mc.player)
                    return@Consumer

                if(mc.player?.velocity?.lengthSquared()!! <= 0.01)
                    firstMove = true

                if (mc.player?.input?.movementInput?.lengthSquared() == 0.0f)
                    return@Consumer

                val accessor = event.velocity as IVec3d

                val prevVelocity = Vec3d(mc.player?.velocity?.x!!, mc.player?.velocity?.y!!, mc.player?.velocity?.z!!)
                val newSpeed = speedValue.value + 0.03 * if (mc.player?.hasStatusEffect(StatusEffects.SPEED)!!) mc.player?.getStatusEffect(StatusEffects.SPEED)?.amplifier!! else 0
                if (mc.player?.isOnGround!!) {
                    if (jumpHeight.value > 0.0) {
                        mc.player?.jump()
                        mc.player?.velocity = mc.player?.velocity?.multiply(1.0, jumpHeight.value, 1.0)
                        val playerVelocityAccessor = mc.player?.velocity as IVec3d
                        playerVelocityAccessor.setX(prevVelocity.x)
                        accessor.setY(mc.player?.velocity?.y!!)
                        playerVelocityAccessor.setZ(prevVelocity.z)
                        if (gainMethod.isSelected(0))
                            speed = newSpeed
                    }
                }
                if (gainMethod.isSelected(1) && timeUtil.hasReached(pulseDelay.value.toLong())) {
                    speed = newSpeed
                    timeUtil.reset()
                }
                if (lowHop.value) {
                    (mc.player?.velocity as IVec3d).setY(prevVelocity.y)
                }
                if (event.velocity.y < 0.0) {
                    accessor.setY(event.velocity.y * gravity.value)
                }

                val baseSpeed = event.velocity.horizontalLength()

                val max = Math.PI * 2
                val temp = (PlayerUtil.getMoveDirection() - moveDir) % max
                val delta = (2 * temp) % (max) - temp

                val maxRotate = Math.toRadians(turnRate.value)

                moveDir += if(firstMove) delta else MathHelper.clamp(delta, -maxRotate, maxRotate)

                firstMove = false

                val moveSpeed = max(speed, baseSpeed)
                accessor.setX(cos(moveDir) * moveSpeed)
                accessor.setZ(sin(moveDir) * moveSpeed)

                speed -= speed / speedDivider.value
            }
            is EventKeyBindingIsPressed -> {
                if (event.keyBinding == mc.options.jumpKey && mc.player?.input?.movementInput?.lengthSquared()!! > 0.0)
                    if (mc.player?.isOnGround!! && jumpHeight.value > 0.0)
                        event.pressed = true
            }
        }
    }

}