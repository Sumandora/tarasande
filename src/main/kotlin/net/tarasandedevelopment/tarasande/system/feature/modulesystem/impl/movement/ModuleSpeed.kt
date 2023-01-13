package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.event.EventJump
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.event.EventMovement
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin


class ModuleSpeed : Module("Speed", "Makes you move faster", ModuleCategory.MOVEMENT) {

    private val jumpHeight = ValueNumber(this, "Jump height", 0.0, 1.0, 2.0, 0.01)
    private val gravity = ValueNumber(this, "Gravity", 0.0, 1.0, 2.0, 0.1)
    private val speedValue = ValueNumber(this, "Speed", 0.0, PlayerUtil.DEFAULT_WALK_SPEED, 1.0, 0.01)
    private val speedDivider = ValueNumber(this, "Speed divider", 1.0, 60.0, 200.0, 1.0)
    private val turnRate = ValueNumber(this, "Turn rate", 0.0, 180.0, 180.0, 1.0)
    private val lowHop = ValueBoolean(this, "Low hop", false)

    private var speed = 0.0
    private var moveDir = 0.0
    private var firstMove = true

    override fun onEnable() {
        firstMove = true
    }

    init {
        registerEvent(EventMovement::class.java) { event ->
            if (event.entity != mc.player)
                return@registerEvent

            if (mc.player?.velocity?.lengthSquared()!! <= 0.01) firstMove = true

            if (!PlayerUtil.isPlayerMoving()) return@registerEvent

            val prevVelocity = mc.player?.velocity?.add(0.0, 0.0, 0.0)!!
            if (mc.player?.isOnGround == true) {
                if (jumpHeight.value > 0.0) {

                    mc.player?.jump()

                    if (!mc.options.jumpKey.pressed)
                        mc.player?.velocity = mc.player?.velocity?.multiply(1.0, jumpHeight.value, 1.0)

                    event.velocity = event.velocity.withAxis(Direction.Axis.Y, mc.player?.velocity?.y!!)

                    mc.player?.velocity = Vec3d(
                        prevVelocity.x,
                        if (lowHop.value && mc.player?.horizontalCollision == false && !mc.options.jumpKey.pressed)
                            prevVelocity.y
                        else
                            mc.player?.velocity?.y!!,
                        prevVelocity.z
                    )

                } else {
                    speed = PlayerUtil.calcBaseSpeed(speedValue.value)
                }
            }
            if (event.velocity.y < 0.0 && !mc.options.jumpKey.pressed) {
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
                speed -= speed / speedDivider.value
        }

        registerEvent(EventJump::class.java) {
            if (it.state == EventJump.State.POST)
                speed = PlayerUtil.calcBaseSpeed(speedValue.value)
        }

        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (!lowHop.value && event.keyBinding == mc.options.jumpKey && PlayerUtil.input.movementInput?.lengthSquared()!! > 0.0)
                if (mc.player?.isOnGround!! && jumpHeight.value > 0.0)
                    event.pressed = true
        }
    }
}
