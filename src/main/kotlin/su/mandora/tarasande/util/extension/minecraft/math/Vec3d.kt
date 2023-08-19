package su.mandora.tarasande.util.extension.minecraft.math

import net.minecraft.util.math.Vec3d

operator fun Vec3d.plus(other: Vec3d): Vec3d {
    return Vec3d(x + other.x, y + other.y, z + other.z)
}

operator fun Vec3d.minus(other: Vec3d): Vec3d {
    return Vec3d(x - other.x, y - other.y, z - other.z)
}

operator fun Vec3d.times(other: Vec3d): Vec3d {
    return Vec3d(x * other.x, y * other.y, z * other.z)
}

operator fun Vec3d.times(other: Number): Vec3d {
    return Vec3d(x * other.toFloat(), y * other.toFloat(), z * other.toFloat())
}

operator fun Vec3d.div(other: Vec3d): Vec3d {
    return Vec3d(x / other.x, y / other.y, z / other.z)
}

operator fun Vec3d.div(other: Number): Vec3d {
    return Vec3d(x / other.toFloat(), y / other.toFloat(), z / other.toFloat())
}

operator fun Vec3d.unaryMinus(): Vec3d {
    return this.negate()
}

fun Vec3d.copy(): Vec3d {
    return Vec3d(x, y, z)
}