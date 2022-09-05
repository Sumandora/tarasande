package su.mandora.tarasande.util.math.rotation

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.value.ValueNumberRange
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.round
import kotlin.math.sqrt

class Rotation(var yaw: Float, var pitch: Float) {

    constructor(other: Rotation) : this(other.yaw, other.pitch)
    constructor(entity: Entity) : this(entity.yaw, entity.pitch)

    companion object {
        fun getGcd(): Double {
            val sensitivity = MinecraftClient.getInstance().options.mouseSensitivity.value * 0.6f.toDouble() + 0.2f.toDouble()
            val sensitivityPow3 = sensitivity * sensitivity * sensitivity
            val sensitivityPow3Mult8 = sensitivityPow3 * 8.0

            return if (MinecraftClient.getInstance().options.perspective.isFirstPerson && MinecraftClient.getInstance().player?.isUsingSpyglass!!)
                sensitivityPow3
            else
                sensitivityPow3Mult8
        }

        fun calculateRotationChange(cursorDeltaX: Double, cursorDeltaY: Double): Rotation {
            val gcd = getGcd()
            return Rotation((-cursorDeltaX * gcd).toFloat() * 0.15f, (-cursorDeltaY * gcd).toFloat() * 0.15f)
        }

        fun approximateCursorDeltas(deltaRotation: Rotation): Pair<Double, Double> {
            val gcd = getGcd() * 0.15f
            return Pair(-round(deltaRotation.yaw / gcd), -round(deltaRotation.pitch / gcd))
        }
    }

    // I can't define prevRotation in the arguments because java wants to call it too

    fun correctSensitivity(): Rotation {
        return correctSensitivity(RotationUtil.fakeRotation ?: Rotation(MinecraftClient.getInstance().player!!))
    }

    fun correctSensitivity(prevRotation: Rotation): Rotation {
        val deltaRotation = calcDelta(prevRotation)

        val cursorDeltas = approximateCursorDeltas(deltaRotation)
        val rotationChange = calculateRotationChange(cursorDeltas.first, cursorDeltas.second)

        yaw = prevRotation.yaw - rotationChange.yaw
        pitch = prevRotation.pitch - rotationChange.pitch
        pitch = MathHelper.clamp(pitch, -90.0f, 90.0f)

        return this
    }

    fun smoothedTurn(target: Rotation, aimSpeed: ValueNumberRange): Rotation {
        return smoothedTurn(target, Pair(aimSpeed.minValue, aimSpeed.maxValue))
    }

    fun smoothedTurn(target: Rotation, aimSpeed: Pair<Double, Double>): Rotation {
        val smoothness = if (aimSpeed.first == 1.0 && aimSpeed.second == 1.0) 1.0
        else MathHelper.clamp((if (aimSpeed.first == aimSpeed.second) aimSpeed.first
        else ThreadLocalRandom.current().nextDouble(aimSpeed.first, aimSpeed.second)) * RenderUtil.deltaTime * 0.05, 0.0, 1.0)
        return smoothedTurn(target, smoothness)
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

    fun forwardVector(dist: Double): Vec3d {
        val f = Math.toRadians(pitch.toDouble()).toFloat()
        val g = Math.toRadians(-yaw.toDouble()).toFloat()
        val h = MathHelper.cos(g)
        val i = MathHelper.sin(g)
        val j = MathHelper.cos(f)
        val k = MathHelper.sin(f)
        return Vec3d(i * j * dist, -k * dist, h * j * dist)
    }


}