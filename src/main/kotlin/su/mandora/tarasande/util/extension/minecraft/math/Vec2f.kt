package su.mandora.tarasande.util.extension.minecraft.math

import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d

operator fun Vec2f.plus(other: Vec2f): Vec2f {
    return this.add(other)
}

operator fun Vec2f.minus(other: Vec2f): Vec2f {
    return Vec2f(x - other.x, y - other.y)
}

operator fun Vec2f.times(other: Vec2f): Vec2f {
    return Vec2f(x * other.x, y * other.y)
}

operator fun Vec2f.times(other: Number): Vec2f {
    return Vec2f(x * other.toFloat(), y * other.toFloat())
}

operator fun Vec2f.div(other: Vec2f): Vec2f {
    return Vec2f(x / other.x, y / other.y)
}

operator fun Vec2f.div(other: Number): Vec2f {
    return Vec2f(x / other.toFloat(), y / other.toFloat())
}

operator fun Vec2f.unaryMinus(): Vec2f {
    return this.negate()
}

fun Vec2f.toVec3d(flipped: Boolean = false): Vec3d {
    return Vec3d((if(!flipped) x else y).toDouble(), 0.0, (if(!flipped) y else x).toDouble())
}