package su.mandora.tarasande.util.math.rotation

import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.event.EventInput
import su.mandora.tarasande.event.EventJump
import su.mandora.tarasande.event.EventUpdate
import su.mandora.tarasande.event.EventVelocityYaw
import su.mandora.tarasande.module.movement.ModuleSprint
import java.util.function.Consumer
import kotlin.math.*

object RotationUtil {

	var fakeRotation: Rotation? = null

	init {
		TarasandeMain.get().managerEvent?.add(Pair(1001, Consumer<Event> { event ->
			if (event is EventJump) {
				if (fakeRotation != null)
					if (TarasandeMain.get().clientValues?.correctMovement?.isSelected(2)!! || TarasandeMain.get().clientValues?.correctMovement?.isSelected(3)!!)
						event.yaw = fakeRotation!!.yaw
			} else if (event is EventVelocityYaw) {
				if (fakeRotation != null)
					if (TarasandeMain.get().clientValues?.correctMovement?.isSelected(2)!! || TarasandeMain.get().clientValues?.correctMovement?.isSelected(3)!!)
						event.yaw = fakeRotation!!.yaw
			} else if (event is EventInput) {
				if (fakeRotation != null)
					if (TarasandeMain.get().clientValues?.correctMovement?.isSelected(3)!!) {
						if (event.movementForward == 0.0f && event.movementSideways == 0.0f)
							return@Consumer

						val realYaw = MinecraftClient.getInstance().player?.yaw!!
						val fakeYaw = fakeRotation?.yaw!!

						val moveX = event.movementSideways * cos(Math.toRadians(realYaw.toDouble())) - event.movementForward * sin(Math.toRadians(realYaw.toDouble()))
						val moveZ = event.movementForward * cos(Math.toRadians(realYaw.toDouble())) + event.movementSideways * sin(Math.toRadians(realYaw.toDouble()))

						var bestMovement: DoubleArray? = null

						for (forward in -1..1)
							for (strafe in -1..1) {
								val newMoveX = strafe * cos(Math.toRadians(fakeYaw.toDouble())) - forward * sin(Math.toRadians(fakeYaw.toDouble()))
								val newMoveZ = forward * cos(Math.toRadians(fakeYaw.toDouble())) + strafe * sin(Math.toRadians(fakeYaw.toDouble()))

								val deltaX = newMoveX - moveX
								val deltaZ = newMoveZ - moveZ

								val dist = deltaX * deltaX + deltaZ * deltaZ

								if (bestMovement == null || bestMovement[0] > dist) {
									bestMovement = doubleArrayOf(dist, forward.toDouble(), strafe.toDouble())
								}
							}

						if (bestMovement != null) {
							event.movementForward = round(bestMovement[1]).toInt().toFloat()
							event.movementSideways = round(bestMovement[2]).toInt().toFloat()
						}
					}
			} else if (event is EventUpdate && event.state == EventUpdate.State.PRE) {
				if (TarasandeMain.get().clientValues!!.correctMovement.isSelected(1) && fakeRotation != null) {
					val allowed = abs(MathHelper.wrapDegrees(MinecraftClient.getInstance().player?.yaw!! - fakeRotation!!.yaw)) <= 45
					TarasandeMain.get().managerModule?.get(ModuleSprint::class.java)?.allowSprint = allowed
					if(MinecraftClient.getInstance().player?.isSprinting!! != allowed) {
						MinecraftClient.getInstance().player?.isSprinting = allowed
						MinecraftClient.getInstance().options.keySprint.isPressed = allowed
					}
				} else {
					TarasandeMain.get().managerModule?.get(ModuleSprint::class.java)?.allowSprint = true
				}
			}
		}))
	}

	fun getRotations(from: Vec3d, to: Vec3d): Rotation {
		val delta = to.subtract(from)
		return Rotation(getYaw(delta).toFloat(), getPitch(delta.y, delta.horizontalLength()).toFloat())
	}

	fun getYaw(fromX: Double, fromZ: Double, toX: Double, toZ: Double) = getYaw(toX - fromX, toZ - fromZ)
	fun getYaw(deltaX: Double, deltaZ: Double) = getYaw(Vec3d(deltaX, 0.0, deltaZ))
	fun getYaw(delta: Vec3d) = Math.toDegrees(atan2(delta.z, delta.x)) - 90

	fun getPitch(deltaY: Double, dist: Double) = -Math.toDegrees(atan2(deltaY, dist))

}