package su.mandora.tarasande.util.math

import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.mc
import kotlin.math.ceil
import kotlin.math.floor

object MathUtil {

    fun closestPointToBox(start: Vec3d, box: Box): Vec3d {
        return Vec3d(
            start.x.coerceIn(box.minX..box.maxX),
            start.y.coerceIn(box.minY..box.maxY),
            start.z.coerceIn(box.minZ..box.maxZ)
        )
    }

    fun getBestAimPoint(box: Box): Vec3d {
        val start = mc.player?.eyePos!!
        if (
            box.minX < start.x && start.x < box.maxX &&
            box.minZ < start.z && start.z < box.maxZ
        )
            return Vec3d(
                box.minX + (box.maxX - box.minX) / 2.0,
                start.y.coerceIn(box.minY..box.maxY),
                box.minZ + (box.maxZ - box.minZ) / 2.0
            )
        return closestPointToBox(start, box)
    }

    fun getBias(time: Double, bias: Double): Double {
        return time / ((1.0 / bias - 2.0) * (1.0 - time) + 1.0)
    }

    fun bringCloser(value: Double, goal: Double, increment: Double): Double {
        return if (value < goal) (value + increment).coerceAtMost(goal) else (value - increment).coerceAtLeast(goal)
    }

    fun roundAwayFromZero(x: Double) =
        if (x < 0)
            floor(x)
        else if (x > 0)
            ceil(x)
        else
            x
}
