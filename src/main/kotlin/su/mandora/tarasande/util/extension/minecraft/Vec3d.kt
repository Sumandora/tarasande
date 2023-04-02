package su.mandora.tarasande.util.extension.minecraft

import net.minecraft.util.math.Vec3d

operator fun Vec3d.plus(other: Vec3d): Vec3d {
    return this.add(other)
}

operator fun Vec3d.minus(other: Vec3d): Vec3d {
    return this.subtract(other)
}

operator fun Vec3d.times(other: Vec3d): Vec3d {
    return this.multiply(other)
}

operator fun Vec3d.times(other: Number): Vec3d {
    return this.multiply(other.toDouble())
}

operator fun Vec3d.div(other: Vec3d): Vec3d {
    return this.multiply(Vec3d(1.0, 1.0, 1.0) / other)
}

operator fun Vec3d.div(other: Number): Vec3d {
    return this.multiply(1.0 / other.toDouble())
}

operator fun Vec3d.unaryMinus(): Vec3d {
    return this.negate()
}

fun Vec3d.copy(): Vec3d {
    return Vec3d(x, y, z)
}