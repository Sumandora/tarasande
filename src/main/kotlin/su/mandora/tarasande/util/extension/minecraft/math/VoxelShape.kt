package su.mandora.tarasande.util.extension.minecraft.math

import net.minecraft.util.math.Box
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes

fun VoxelShape.boundingBox(): Box {
    if (isEmpty)
        return Box()
    return boundingBox
}

fun VoxelShape.neverEmpty(): VoxelShape {
    if (isEmpty)
        return VoxelShapes.fullCube()
    return this
}