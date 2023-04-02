package su.mandora.tarasande.util.extension.minecraft

import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

fun HitResult?.isEntityHitResult(): Boolean {
    return this != null && this.type == HitResult.Type.ENTITY && this is EntityHitResult
}

fun HitResult?.isBlockHitResult(): Boolean {
    return this != null && this.type == HitResult.Type.BLOCK && this is BlockHitResult
}

fun HitResult?.isMissHitResult(): Boolean {
    return this == null || this.type == HitResult.Type.MISS
}

fun HitResult?.isSame(side: Direction, blockPos: BlockPos): Boolean {
    if (!isBlockHitResult())
        return false
    this as BlockHitResult
    return this.side == side.hitResultSide() && this.blockPos == blockPos
}