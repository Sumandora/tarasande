package su.mandora.tarasande.feature.rotation.api

import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.util.extension.minecraft.math.minus
import kotlin.math.atan2

object RotationUtil {

    fun getRotations(from: Vec3d, to: Vec3d): Rotation {
        val delta = to - from
        return getRotations(delta)
    }

    fun getRotations(delta: Vec3d): Rotation {
        return Rotation(getYaw(delta).toFloat(), getPitch(delta.y, delta.horizontalLength()).toFloat())
    }

    fun getYaw(fromX: Double, fromZ: Double, toX: Double, toZ: Double) = getYaw(toX - fromX, toZ - fromZ)
    fun getYaw(deltaX: Double, deltaZ: Double) = getYaw(Vec2f(deltaX.toFloat(), deltaZ.toFloat()))
    fun getYaw(delta: Vec3d) = Math.toDegrees(atan2(delta.z, delta.x)) - 90
    fun getYaw(delta: Vec2f) = Math.toDegrees(atan2(delta.y, delta.x).toDouble()) - 90

    private fun getPitch(deltaY: Double, dist: Double) = -Math.toDegrees(atan2(deltaY, dist))
}