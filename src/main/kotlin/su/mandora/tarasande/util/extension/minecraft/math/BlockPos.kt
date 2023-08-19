package su.mandora.tarasande.util.extension.minecraft.math

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

fun BlockPos(pos: Vec3d): BlockPos {
    return BlockPos.ofFloored(pos.x, pos.y, pos.z)
}
