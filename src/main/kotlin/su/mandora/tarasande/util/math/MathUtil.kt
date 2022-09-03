package su.mandora.tarasande.util.math

import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d

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

    fun calculateBezierCurve(accuracy: Int, points: ArrayList<Vec2f>): ArrayList<Vec2f> {
        if (points.size < 2) return points
        val list = ArrayList<Vec2f>()
        val origLines = ArrayList<Pair<Vec2f, Vec2f>>()
        var prev = points[0]
        for (point in points.subList(1, points.size)) {
            origLines.add(Pair(prev, point))
            prev = point
        }
        var t = 0.0f
        while (t <= 1.0f) {
            var lines = ArrayList(origLines)
            while (lines.size > 1) {
                val newLines = ArrayList<Pair<Vec2f, Vec2f>>()
                var prev = lines[0].first.add(lines[0].second.add(lines[0].first.negate()).multiply(t))
                for (line in lines.subList(1, lines.size)) {
                    val new = line.first.add(line.second.add(line.first.negate()).multiply(t))
                    newLines.add(Pair(prev, new))
                    prev = new
                }
                lines = newLines
            }
            list.add(lines[0].first.add(lines[0].second.add(lines[0].first.negate()).multiply(t)))

            t += 1.0f / accuracy
        }
        return list
    }

}