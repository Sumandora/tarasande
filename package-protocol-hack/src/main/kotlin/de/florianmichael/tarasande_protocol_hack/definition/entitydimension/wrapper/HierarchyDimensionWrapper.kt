package de.florianmichael.tarasande_protocol_hack.definition.entitydimension.wrapper

import de.florianmichael.tarasande_protocol_hack.definition.entitydimension.base.EntityDimension
import net.minecraft.entity.Entity

open class HierarchyDimensionWrapper : EntityDimensionWrapper {
    private val dimensionMap = LinkedHashMap<Class<out Entity>, EntityDimension<*>>()

    init {
        initDimensions()
    }

    open fun initDimensions() {}

    fun <T : Entity> register(clazz: Class<T>, dimension: EntityDimension<T>) {
        dimensionMap[clazz] = dimension
    }

    override fun <T : Entity> getDimension(obj: T): EntityDimension<T>? {
        for (entry in dimensionMap.entries) {
            if (entry.key.isInstance(obj)) {
                @Suppress("UNCHECKED_CAST")
                return entry.value as? EntityDimension<T>
            }
        }
        return null
    }
}
