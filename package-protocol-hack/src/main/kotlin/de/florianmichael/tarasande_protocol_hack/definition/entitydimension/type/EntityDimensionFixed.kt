package de.florianmichael.tarasande_protocol_hack.definition.entitydimension.type

import de.florianmichael.tarasande_protocol_hack.definition.entitydimension.base.EntityDimension
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityPose

class EntityDimensionFixed<T : Entity>(private val width: Float, private val height: Float) : EntityDimension<T> {

    override fun getWidth(t: Entity, pose: EntityPose) = width
    override fun getHeight(t: Entity, pose: EntityPose) = height
}
