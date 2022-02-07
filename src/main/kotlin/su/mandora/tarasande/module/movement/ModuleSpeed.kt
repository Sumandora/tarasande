package su.mandora.tarasande.module.movement

import net.minecraft.entity.effect.StatusEffects
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
import java.util.concurrent.ThreadLocalRandom
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

	private val timeUtil = TimeUtil()
	private var speed = 0.0

	val eventConsumer = Consumer<Event> { event ->
		when {
			event is EventMovement && event.entity == mc.player && mc.player?.input?.movementInput?.lengthSquared()!! > 0.0 -> {
				val accessor = event.velocity as IVec3d
				val baseSpeed = event.velocity.horizontalLength()

				val movementDirection = PlayerUtil.getMoveDirection()

				val moveSpeed = max(speed, baseSpeed)
				accessor.setX(cos(movementDirection) * moveSpeed)
				accessor.setZ(sin(movementDirection) * moveSpeed)

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

				speed -= speed / speedDivider.value
			}
			event is EventKeyBindingIsPressed && event.keyBinding == mc.options.keyJump && mc.player?.input?.movementInput?.lengthSquared()!! > 0.0 -> {
				event.pressed = event.pressed || (mc.player?.isOnGround!! && jumpHeight.value > 0.0)
			}
		}
	}

}