package de.florianmichael.tarasande_protocol_hack.definition.entitydimension.base

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityPose
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

interface EntityDimension<T : Entity> {

    fun getWidth(t: Entity, pose: EntityPose): Float
    fun getHeight(t: Entity, pose: EntityPose): Float

    fun getBoxAt(t: Entity, pose: EntityPose, x: Double, y: Double, z: Double): Box {
        val f = getWidth(t, pose) / 2.0F
        val f1 = getHeight(t, pose)

        return Box(x - f.toDouble(), y, z - f.toDouble(), x + f.toDouble(), y + f1.toDouble(), z + f.toDouble())
    }

    fun getBoxAt(t: Entity, pose: EntityPose, pos: Vec3d) = getBoxAt(t, pose, pos.x, pos.y, pos.z)
}