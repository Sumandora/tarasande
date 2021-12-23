package su.mandora.tarasande.util.math.rotation

import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.MathHelper
import kotlin.math.round
import kotlin.math.sqrt

class Rotation(var yaw: Float, var pitch: Float) {

	constructor(other: Rotation) : this(other.yaw, other.pitch)

	// I can't define prevRotation in the arguments because java wants to call it too

	fun correctSensitivity(): Rotation {
		return correctSensitivity(RotationUtil.fakeRotation ?: Rotation(MinecraftClient.getInstance().player?.yaw!!, MinecraftClient.getInstance().player?.pitch!!))
	}

	fun correctSensitivity(prevRotation: Rotation): Rotation {
		val sensitivity = MinecraftClient.getInstance().options.mouseSensitivity * 0.6000000238418579 + 0.20000000298023224
		val sensitivityPow3 = sensitivity * sensitivity * sensitivity
		val gcd = sensitivityPow3 * 8.0

		val deltaRotation = calcDelta(prevRotation)

		val cursorDeltaX = round(deltaRotation.yaw / 0.15 / gcd).toInt() * gcd
		val cursorDeltaY = round(deltaRotation.pitch / 0.15 / gcd).toInt() * gcd

		val g = cursorDeltaX.toFloat() * 0.15f
		val f = cursorDeltaY.toFloat() * 0.15f

		yaw = prevRotation.yaw - g
		pitch = prevRotation.pitch - f
		pitch = MathHelper.clamp(pitch, -90.0f, 90.0f)
		return this
	}

	fun smoothedTurn(target: Rotation, smoothness: Double): Rotation {
		val deltaRotation = calcDelta(target)
		yaw += deltaRotation.yaw * smoothness.toFloat()
		pitch += deltaRotation.pitch * smoothness.toFloat()
		return this
	}

	fun fov(other: Rotation): Float {
		return dist(calcDelta(other))
	}

	fun calcDelta(other: Rotation): Rotation {
		return Rotation(MathHelper.wrapDegrees(other.yaw - yaw), other.pitch - pitch)
	}

	fun dist(deltaRotation: Rotation): Float {
		return sqrt(deltaRotation.yaw * deltaRotation.yaw + deltaRotation.pitch * deltaRotation.pitch)
	}

	override fun toString(): String {
		return "Rotation(yaw=$yaw, pitch=$pitch)"
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Rotation

		if (yaw != other.yaw) return false
		if (pitch != other.pitch) return false

		return true
	}

	override fun hashCode(): Int {
		var result = yaw.hashCode()
		result = 31 * result + pitch.hashCode()
		return result
	}


}