package su.mandora.tarasande.util.extension.minecraft

import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d

fun Entity.prevPos(): Vec3d {
    return Vec3d(prevX, prevY, prevZ)
}