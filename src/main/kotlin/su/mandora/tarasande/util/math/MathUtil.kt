package su.mandora.tarasande.util.math

import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import kotlin.math.ceil
import kotlin.math.floor

object MathUtil {

    fun closestPointToBox(start: Vec3d, box: Box): Vec3d {
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

    fun roundAwayFromZero(x: Double) =
        if (x < 0)
            floor(x)
        else if (x > 0)
            ceil(x)
        else
            x

}