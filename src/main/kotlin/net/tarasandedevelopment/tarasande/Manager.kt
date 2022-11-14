package net.tarasandedevelopment.tarasande

import java.util.concurrent.CopyOnWriteArrayList

open class Manager<T : Any> {
    val list = CopyOnWriteArrayList<T>()

    fun add(obj: T) {
        insert(obj, list.size)
    }

    fun add(vararg objects: T) {
        objects.forEach { add(it) }
    }

    open fun insert(obj: T, index: Int) {
        if (!list.contains(obj))
            list.add(index, obj)
    }

    fun rem(vararg objects: T) = list.removeAll(objects.toSet())

    open fun <T> get(clazz: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return list.first { it.javaClass == clazz } as T
    }
}