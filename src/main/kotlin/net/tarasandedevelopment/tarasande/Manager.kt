package net.tarasandedevelopment.tarasande

import java.util.concurrent.CopyOnWriteArrayList

open class Manager<T : Any> {
    val list = CopyOnWriteArrayList<T>()

    open fun add(obj: T) {
        if (!list.contains(obj))
            list.add(obj)
    }

    fun add(vararg objects: T) {
        objects.forEach { add(it) }
    }

    fun rem(vararg objects: T) = list.removeAll(objects.toSet())

    open fun <T> get(clazz: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return list.first { it.javaClass == clazz } as T
    }
}