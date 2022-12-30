package net.tarasandedevelopment.tarasande.util.extension.minecraft

import net.minecraft.util.math.Box
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes

fun VoxelShape.boundingBox(): Box {
    if(isEmpty)
        return Box()
    return boundingBox
}

fun VoxelShape.neverEmpty(): VoxelShape {
    if(isEmpty)
        return VoxelShapes.fullCube()
    return this
}