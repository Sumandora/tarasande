package su.mandora.tarasande.module.movement

import net.minecraft.entity.effect.StatusEffects
import net.minecraft.util.math.MathHelper
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventJump
import su.mandora.tarasande.event.EventKeyBindingIsPressed
import su.mandora.tarasande.event.EventMovement
import su.mandora.tarasande.mixin.accessor.IKeyBinding
import su.mandora.tarasande.mixin.accessor.IVec3d
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin


class ModuleSpeed : Module("Speed", "Makes you move faster", ModuleCategory.MOVEMENT) {

    private val jumpHeight = ValueNumber(this, "Jump height", 0.0, 1.0, 2.0, 0.01)
    private val gravity = ValueNumber(this, "Gravity", 0.0, 1.0, 2.0, 0.1)
    private val speedValue = ValueNumber(this, "Speed", 0.0, 0.28, 1.0, 0.01)
    private val speedDivider = ValueNumber(this, "Speed divider", 1.0, 60.0, 200.0, 1.0)
    private val turnRate = ValueNumber(this, "Turn rate", 0.0, 180.0, 180.0, 1.0)

    private var speed = 0.0
    private var moveDir = 0.0
    private var firstMove = true

    private fun calcSpeed(): Double = speedValue.value + 0.03 * if (mc.player?.hasStatusEffect(StatusEffects.SPEED)!!) mc.player?.getStatusEffect(StatusEffects.SPEED)?.amplifier!! else 0

    override fun onEnable() {
        firstMove = true
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventMovement -> {
                if (event.entity != mc.player) return@Consumer

                if (mc.player?.velocity?.lengthSquared()!! <= 0.01) firstMove = true

                if (mc.player?.input?.movementInput?.lengthSquared() == 0.0f) return@Consumer

                val accessor = event.velocity as IVec3d

                val prevVelocity = mc.player?.velocity?.add(0.0, 0.0, 0.0)!!
                if (mc.player?.isOnGround!!) {
                    if (jumpHeight.value > 0.0) {
                        mc.player?.jump()
                        if(!(mc.options.jumpKey as IKeyBinding).tarasande_forceIsPressed())
                            mc.player?.velocity = mc.player?.velocity?.multiply(1.0, jumpHeight.value, 1.0)
                        val playerVelocityAccessor = mc.player?.velocity as IVec3d
                        playerVelocityAccessor.tarasande_setX(prevVelocity.x)
                        accessor.tarasande_setY(mc.player?.velocity?.y!!)
                        playerVelocityAccessor.tarasande_setZ(prevVelocity.z)
                    }
                }
                if (event.velocity.y < 0.0) {
                    accessor.tarasande_setY(event.velocity.y * gravity.value)
                }

                val baseSpeed = event.velocity.horizontalLength()

                val max = Math.PI * 2
                val temp = (PlayerUtil.getMoveDirection() + PI / 2 - moveDir) % max
                val delta = (2 * temp) % (max) - temp

                val maxRotate = Math.toRadians(turnRate.value)

                moveDir += if (firstMove) delta else MathHelper.clamp(delta, -maxRotate, maxRotate)

                firstMove = false

                val moveSpeed = max(speed, baseSpeed)
                accessor.tarasande_setX(cos(moveDir) * moveSpeed)
                accessor.tarasande_setZ(sin(moveDir) * moveSpeed)

                speed -= speed / speedDivider.value
            }

            is EventJump -> {
                speed = calcSpeed()
            }

            is EventKeyBindingIsPressed -> {
                if (event.keyBinding == mc.options.jumpKey && mc.player?.input?.movementInput?.lengthSquared()!! > 0.0) if (mc.player?.isOnGround!! && jumpHeight.value > 0.0) event.pressed = true
            }
        }
    }

}