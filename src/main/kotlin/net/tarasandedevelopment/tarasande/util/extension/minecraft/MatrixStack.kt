package net.tarasandedevelopment.tarasande.util.extension.minecraft

import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3d

fun MatrixStack.getPositionVec3d() = peek().positionMatrix.let { Vec3d(it.m30().toDouble(), it.m31().toDouble(), it.m32().toDouble()) }
