package net.tarasandedevelopment.tarasande.util.extension.minecraft

import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3d

fun MatrixStack.getPositionVec3d() = Vec3d(peek().positionMatrix.m30().toDouble(), peek().positionMatrix.m31().toDouble(), peek().positionMatrix.m32().toDouble())
