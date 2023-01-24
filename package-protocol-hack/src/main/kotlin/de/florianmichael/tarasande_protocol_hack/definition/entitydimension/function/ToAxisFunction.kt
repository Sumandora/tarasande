package de.florianmichael.tarasande_protocol_hack.definition.entitydimension.function

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityPose

interface ToAxisFunction<T : Entity> {

    fun getAxisLength(t: Entity, pose: EntityPose): Float
}