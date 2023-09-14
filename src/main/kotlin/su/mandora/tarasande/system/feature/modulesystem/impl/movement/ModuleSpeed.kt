package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.event.impl.EventEntityHurt
import su.mandora.tarasande.event.impl.EventInput
import su.mandora.tarasande.event.impl.EventJump
import su.mandora.tarasande.event.impl.EventMovement
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.DEFAULT_WALK_SPEED
import su.mandora.tarasande.util.extension.minecraft.forceIsPressed
import su.mandora.tarasande.util.extension.minecraft.math.copy
import su.mandora.tarasande.util.player.PlayerUtil
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin


class ModuleSpeed : Module("Speed", "Makes you move faster", ModuleCategory.MOVEMENT) {

    private val jumpHeight = ValueNumber(this, "Jump height", 0.0, 1.0, 2.0, 0.01)
    private val gravity = ValueNumber(this, "Gravity", 0.0, 1.0, 2.0, 0.1)
    private val speedValue = ValueNumber(this, "Speed", 0.0, DEFAULT_WALK_SPEED, 1.0, 0.01)
    private val airFriction = ValueNumber(this, "Air friction", 1.0, 160.0, 200.0, 1.0)
    private val turnRate = ValueNumber(this, "Turn rate", 0.0, 180.0, 180.0, 1.0)
    private val lowHop = ValueBoolean(this, "Low hop", false)
    private val damageBoost = ValueBoolean(this, "Damage boost", false)
    private val boostAmount = ValueNumber(this, "Boost amount", 0.0, DEFAULT_WALK_SPEED, 1.0, 0.01, isEnabled = { damageBoost.value })
    private val hardStop = ValueBoolean(this, "Hard stop", true)
    private val wallDetection = ValueBoolean(this, "Wall detection", true, isEnabled = { jumpHeight.value != 1.0 || lowHop.value })

    private var speed = 0.0
    private var moveDir = 0.0
    private var firstMove = true

    override fun onEnable() {
        firstMove = true
        speed = DEFAULT_WALK_SPEED
    }

    init {
        registerEvent(EventMovement::class.java) { event ->
            if (event.entity != mc.player)
                return@registerEvent

            if (mc.player?.velocity?.lengthSquared()!! <= 0.01) firstMove = true

            if (!PlayerUtil.isPlayerMoving()) {
                if(hardStop.value)
                    event.velocity = Vec3d.ZERO.withAxis(Direction.Axis.Y, event.velocity.y)

                return@registerEvent
            }

            val prevVelocity = mc.player?.velocity?.copy()!!
            if (mc.player?.isOnGround == true) {
                if (jumpHeight.value > 0.0) {
                    mc.player?.jump()

                    val collided = (wallDetection.value && mc.player!!.horizontalCollision) || mc.options.jumpKey.forceIsPressed()

                    event.velocity = event.velocity.withAxis(Direction.Axis.Y, mc.player?.velocity?.y!! * if(collided) 1.0 else jumpHeight.value)

                    mc.player?.velocity = Vec3d(
                        prevVelocity.x,
                        if (lowHop.value && !collided)
                            prevVelocity.y
                        else
                            event.velocity.y,
                        prevVelocity.z
                    )

                } else {
                    speed = PlayerUtil.calcBaseSpeed(speedValue.value)
                }
            }
            if (event.velocity.y < 0.0 && !mc.options.jumpKey.forceIsPressed()) {
                event.velocity = event.velocity.multiply(1.0, gravity.value, 1.0)
            }

            val baseSpeed = event.velocity.horizontalLength()

            val goal = PlayerUtil.getMoveDirection()

            moveDir = if (firstMove) goal else moveDir + MathHelper.clamp(MathHelper.wrapDegrees(goal - moveDir), -turnRate.value, turnRate.value)

            firstMove = false

            val moveSpeed = max(speed, baseSpeed)
            val rad = Math.toRadians(moveDir + 90)

            event.velocity = Vec3d(
                cos(rad) * moveSpeed,
                event.velocity.y,
                sin(rad) * moveSpeed
            )

            if (mc.player?.isOnGround == false)
                speed -= speed / airFriction.value
        }

        registerEvent(EventJump::class.java) { event ->
            if (event.state == EventJump.State.POST)
                speed = PlayerUtil.calcBaseSpeed(speedValue.value)
        }

        registerEvent(EventEntityHurt::class.java) { event ->
            if (event.entity == mc.player && damageBoost.value)
                speed = boostAmount.value
        }

        registerEvent(EventInput::class.java) { event ->
            if(event.input == mc.player?.input)
                if (!lowHop.value && PlayerUtil.isPlayerMoving())
                    if (mc.player!!.isOnGround && jumpHeight.value > 0.0)
                        event.input.jumping = true
        }
    }
}
