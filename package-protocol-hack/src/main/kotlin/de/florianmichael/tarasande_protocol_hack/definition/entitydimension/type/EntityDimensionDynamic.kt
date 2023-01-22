package de.florianmichael.tarasande_protocol_hack.definition.entitydimension.type

import de.florianmichael.tarasande_protocol_hack.definition.entitydimension.base.EntityDimension
import de.florianmichael.tarasande_protocol_hack.definition.entitydimension.ToAxisFunction
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityPose

class EntityDimensionDynamic<T : Entity>(private val widthFunction: ToAxisFunction<T>, private val heightFunction: ToAxisFunction<T>) :
    EntityDimension<T> {

    override fun getWidth(t: Entity, pose: EntityPose) = widthFunction.getAxisLength(t, pose)
    override fun getHeight(t: Entity, pose: EntityPose) = heightFunction.getAxisLength(t, pose)
}
