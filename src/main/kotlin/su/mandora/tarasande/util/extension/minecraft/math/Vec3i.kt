package su.mandora.tarasande.util.extension.minecraft.math

import net.minecraft.util.math.Vec3i

operator fun Vec3i.unaryMinus() = Vec3i(-x, -y, -z)