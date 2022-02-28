package su.mandora.tarasande.util.math

import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d

object MathUtil {

    fun closestPointToBox(start: Vec3d, box: Box): Vec3d {
        if (
            box.minX < start.x && start.x < box.maxX &&
            box.minZ < start.z && start.z < box.maxZ
        )
            return Vec3d(
                box.minX + (box.maxX - box.minX) / 2.0,
                MathHelper.clamp(start.y, box.minY, box.maxY),
                box.minZ + (box.maxZ - box.minZ) / 2.0
            )
        return Vec3d(
            MathHelper.clamp(start.x, box.minX, box.maxX),
            MathHelper.clamp(start.y, box.minY, box.maxY),
            MathHelper.clamp(start.z, box.minZ, box.maxZ)
        )
    }

    fun getBias(time: Double, bias: Double): Double {
        return time / ((1.0 / bias - 2.0) * (1.0 - time) + 1.0)
    }

    fun bringCloser(value: Double, goal: Double, increment: Double): Double {
        return if (value < goal) (value + increment).coerceAtMost(goal) else (value - increment).coerceAtLeast(goal)
    }

    fun getRotationVector(pitch: Float, yaw: Float): Vec3d {
        val f = pitch * (Math.PI.toFloat() / 180)
        val g = -yaw * (Math.PI.toFloat() / 180)
        val h = MathHelper.cos(g)
        val i = MathHelper.sin(g)
        val j = MathHelper.cos(f)
        val k = MathHelper.sin(f)
        return Vec3d((i * j).toDouble(), (-k).toDouble(), (h * j).toDouble())
    }

}