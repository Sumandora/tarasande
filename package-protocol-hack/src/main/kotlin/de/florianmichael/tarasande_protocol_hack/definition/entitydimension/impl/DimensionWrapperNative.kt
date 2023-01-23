package de.florianmichael.tarasande_protocol_hack.definition.entitydimension.impl

import de.florianmichael.tarasande_protocol_hack.definition.entitydimension.base.EntityDimension
import de.florianmichael.tarasande_protocol_hack.definition.entitydimension.wrapper.EntityDimensionWrapper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityPose

class DimensionWrapperNative : EntityDimensionWrapper {

    override fun <T : Entity> getDimension(obj: T): EntityDimension<T> {
        return object : EntityDimension<T> {
            override fun getWidth(t: Entity, pose: EntityPose) = obj.getDimensions(pose).width
            override fun getHeight(t: Entity, pose: EntityPose) = obj.getDimensions(pose).height

            override fun getBoxAt(t: Entity, pose: EntityPose, x: Double, y: Double, z: Double) = obj.getDimensions(pose).getBoxAt(x, y, z)
        }
    }
}
