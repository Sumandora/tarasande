package su.mandora.tarasande.util.math.rotation

import net.minecraft.entity.Entity
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumberRange
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule
import su.mandora.tarasande.system.feature.modulesystem.impl.exploit.ModuleNoPitchLimit
import su.mandora.tarasande.util.extension.kotlinruntime.prefer
import su.mandora.tarasande.util.render.RenderUtil
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.*

class Rotation {

    val yaw: Float
    val pitch: Float

    constructor(yaw: Float, pitch: Float) {
        this.yaw = yaw
        this.pitch = pitch
    }

    constructor(entity: Entity) : this(entity.yaw, entity.pitch)

    companion object {
        const val MAXIMUM_DELTA = 255.0 // sqrt(180 * 180 + 180 * 180)

        private fun getGcd(): Double {
            val sensitivity = mc.options.mouseSensitivity.value * 0.6F.toDouble() + 0.2F.toDouble()
            val sensitivityPow3 = sensitivity * sensitivity * sensitivity
            val sensitivityPow3Mult8 = sensitivityPow3 * 8.0

            return if (mc.options.perspective.isFirstPerson && mc.player!!.isUsingSpyglass)
                sensitivityPow3
            else
                sensitivityPow3Mult8
        }

        fun calculateNewRotation(prevRotation: Rotation, cursorDeltas: Pair<Double, Double>): Rotation {
            val gcd = getGcd()
            val rotationChange = Rotation((cursorDeltas.first * gcd).toFloat() * 0.15F, (cursorDeltas.second * gcd).toFloat() * 0.15F)
            var newRotation = prevRotation + rotationChange
            if (!ManagerModule.get(ModuleNoPitchLimit::class.java).enabled.value)
                newRotation = newRotation.withPitch(newRotation.pitch.coerceIn(-90.0F, 90.0F))
            return newRotation
        }

        fun approximateCursorDeltas(deltaRotation: Rotation): Array<Pair<Double, Double>> {
            val gcd = getGcd() * 0.15F
            val targetX = -deltaRotation.yaw / gcd
            val targetY = -deltaRotation.pitch / gcd
            return arrayOf(
                Pair(floor(targetX), floor(targetY)),
                Pair(ceil(targetX), floor(targetY)),
                Pair(ceil(targetX), ceil(targetY)),
                Pair(floor(targetX), ceil(targetY))
            )
        }
    }

    fun correctSensitivity(prevRotation: Rotation = Rotations.fakeRotation ?: Rotation(mc.player!!), preference: ((Rotation) -> Boolean)? = null): Rotation {
        val deltaRotation = closestDelta(prevRotation)
        val cursorDeltas = approximateCursorDeltas(deltaRotation)
        val newRotations = cursorDeltas.map { calculateNewRotation(prevRotation, it) }

        return if (preference == null)
            newRotations.minBy { fov(it) }
        else
            newRotations.prefer(preference)
    }

    fun smoothedTurn(target: Rotation, aimSpeed: ValueNumberRange): Rotation {
        return smoothedTurn(target, Pair(aimSpeed.minValue, aimSpeed.maxValue))
    }

    fun smoothedTurn(target: Rotation, aimSpeed: Pair<Double, Double>): Rotation {
        val smoothness =
            if (aimSpeed.first == 1.0 && aimSpeed.second == 1.0)
                1.0
            else {
                val randomAimSpeed =
                    if (aimSpeed.first == aimSpeed.second)
                        aimSpeed.first
                    else
                        ThreadLocalRandom.current().nextDouble(aimSpeed.first, aimSpeed.second)

                (randomAimSpeed * RenderUtil.deltaTime * 0.05).coerceIn(0.0..1.0)
            }
        return smoothedTurn(target, smoothness)
    }

    fun smoothedTurn(target: Rotation, smoothness: Double): Rotation {
        val deltaRotation = closestDelta(target) * smoothness.toFloat()
        return this + deltaRotation
    }

    fun fov(other: Rotation): Float {
        return dist(closestDelta(other))
    }

    fun closestDelta(other: Rotation): Rotation {
        return Rotation(MathHelper.wrapDegrees(other.yaw - yaw), other.pitch - pitch)
    }

    private fun dist(deltaRotation: Rotation): Float {
        return sqrt(deltaRotation.yaw * deltaRotation.yaw + deltaRotation.pitch * deltaRotation.pitch)
    }

    fun forwardVector(): Vec3d {
        val yawRad = Math.toRadians(yaw.toDouble())
        val pitchRad = Math.toRadians(pitch.toDouble())
        return Vec3d(
            sin(-yawRad) * cos(pitchRad),
            -sin(pitchRad),
            cos(-yawRad) * cos(pitchRad)
        )
    }

    fun withYaw(yaw: Float): Rotation {
        return Rotation(yaw, pitch)
    }

    fun withPitch(pitch: Float): Rotation {
        return Rotation(yaw, pitch)
    }

    operator fun plus(other: Rotation) = Rotation(yaw + other.yaw, pitch + other.pitch)
    operator fun minus(other: Rotation) = Rotation(yaw - other.yaw, pitch - other.pitch)
    operator fun times(value: Float) = Rotation(yaw * value, pitch * value)

    override fun toString(): String {
        return "Rotation(yaw=$yaw, pitch=$pitch)"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Rotation) return false

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