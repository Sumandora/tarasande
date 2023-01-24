package de.florianmichael.tarasande_protocol_hack.definition.entitydimension.wrapper

import de.florianmichael.tarasande_protocol_hack.definition.entitydimension.base.EntityDimension
import net.minecraft.entity.Entity

interface EntityDimensionWrapper {

    fun <T : Entity> getDimension(obj: T): EntityDimension<T>?
    fun <T : Entity> getEyeHeight(obj: T): ((t: Entity) -> Float)?

}
