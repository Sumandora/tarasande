package net.tarasandedevelopment.tarasande.util.extension.minecraft

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

operator fun Vec3d.times(mult: Number): Vec3d {
    return this.multiply(mult.toDouble())
}

operator fun Vec3d.div(other: Vec3d): Vec3d {
    return this.multiply(Vec3d(1.0, 1.0, 1.0) / other)
}

operator fun Vec3d.div(mult: Number): Vec3d {
    return this.multiply(1.0 / mult.toDouble())
}

operator fun Vec3d.unaryMinus(): Vec3d {
    return this.negate()
}