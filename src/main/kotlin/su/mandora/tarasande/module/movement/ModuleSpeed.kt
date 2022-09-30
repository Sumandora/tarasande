package su.mandora.tarasande.module.movement

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
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin


class ModuleSpeed : Module("Speed", "Makes you move faster", ModuleCategory.MOVEMENT) {

    private val jumpHeight = ValueNumber(this, "Jump height", 0.0, 1.0, 2.0, 0.01)
    private val gravity = ValueNumber(this, "Gravity", 0.0, 1.0, 2.0, 0.1)
    private val speedValue = ValueNumber(this, "Speed", 0.0, PlayerUtil.walkSpeed, 1.0, 0.01)
    private val speedDivider = ValueNumber(this, "Speed divider", 1.0, 60.0, 200.0, 1.0)
    private val turnRate = ValueNumber(this, "Turn rate", 0.0, 180.0, 180.0, 1.0)
    private val lowHop = ValueBoolean(this, "Low hop", false)

    private var speed = 0.0
    private var moveDir = 0.0
    private var firstMove = true

    override fun onEnable() {
        firstMove = true
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventMovement -> {
                if (event.entity != mc.player) return@Consumer

                if (mc.player?.velocity?.lengthSquared()!! <= 0.01) firstMove = true

                if (PlayerUtil.input.movementInput?.lengthSquared() == 0.0f) return@Consumer

                val accessor = event.velocity as IVec3d

                val prevVelocity = mc.player?.velocity?.add(0.0, 0.0, 0.0)!!
                if (mc.player?.isOnGround == true) {
                    if (jumpHeight.value > 0.0) {

                        mc.player?.jump()

                        if (!(mc.options.jumpKey as IKeyBinding).tarasande_forceIsPressed())
                            mc.player?.velocity = mc.player?.velocity?.multiply(1.0, jumpHeight.value, 1.0)

                        accessor.tarasande_setY(mc.player?.velocity?.y!!)

                        val playerVelocityAccessor = mc.player?.velocity as IVec3d
                        playerVelocityAccessor.tarasande_setX(prevVelocity.x)
                        if (lowHop.value && mc.player?.horizontalCollision == false)
                            playerVelocityAccessor.tarasande_setY(prevVelocity.y)
                        playerVelocityAccessor.tarasande_setZ(prevVelocity.z)

                    } else {
                        speed = PlayerUtil.calcBaseSpeed(speedValue.value)
                    }
                }
                if (event.velocity.y < 0.0) {
                    accessor.tarasande_setY(event.velocity.y * gravity.value)
                }

                val baseSpeed = event.velocity.horizontalLength()

                val goal = PlayerUtil.getMoveDirection()

                moveDir = if (firstMove) goal else moveDir + MathHelper.clamp(MathHelper.wrapDegrees(goal - moveDir), -turnRate.value, turnRate.value)

                firstMove = false

                val moveSpeed = max(speed, baseSpeed)
                val rad = Math.toRadians(moveDir + 90)
                accessor.tarasande_setX(cos(rad) * moveSpeed)
                accessor.tarasande_setZ(sin(rad) * moveSpeed)

                if (mc.player?.isOnGround == false)
                    speed -= speed / speedDivider.value
            }

            is EventJump -> {
                speed = PlayerUtil.calcBaseSpeed(speedValue.value)
            }

            is EventKeyBindingIsPressed -> {
                if (!lowHop.value && event.keyBinding == mc.options.jumpKey && PlayerUtil.input.movementInput?.lengthSquared()!! > 0.0)
                    if (mc.player?.isOnGround!! && jumpHeight.value > 0.0)
                        event.pressed = true
            }
        }
    }

}