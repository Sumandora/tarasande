package de.florianmichael.tarasande_protocol_hack.definition.entitydimension.wrapper

import de.florianmichael.tarasande_protocol_hack.definition.entitydimension.base.EntityDimension
import net.minecraft.entity.Entity

open class HierarchyDimensionWrapper : EntityDimensionWrapper {
    private val dimensionMap = LinkedHashMap<Class<out Entity>, EntityDimension<*>>()
    private val eyeHeightMap = LinkedHashMap<Class<out Entity>, (t: Entity) -> Float>()

    init {
        this.init()
    }

    open fun init() {}

    fun <T : Entity> registerDimension(clazz: Class<T>, dimension: EntityDimension<T>) {
        dimensionMap[clazz] = dimension
    }

    fun <T : Entity> registerEyeHeight(clazz: Class<T>, eyeHeight: (t: Entity) -> Float) {
        eyeHeightMap[clazz] = eyeHeight
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

    override fun <T : Entity> getEyeHeight(obj: T): ((Entity) -> Float)? {
        for (entry in eyeHeightMap.entries) {
            if (entry.key.isInstance(obj)) {
                @Suppress("UNCHECKED_CAST")
                return entry.value
            }
        }
        return null
    }
}
