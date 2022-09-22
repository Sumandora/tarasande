package su.mandora.tarasande.base

import java.util.concurrent.CopyOnWriteArrayList

open class Manager<T : Any> {
    val list = CopyOnWriteArrayList<T>()

    open fun add(vararg objects: T) {
        for (obj in objects) {
            if (!list.contains(obj))
                list.add(obj)
        }
    }

    fun rem(vararg objects: T) = list.removeAll(objects.toSet())

    fun <T> get(clazz: Class<T>): T {
        for (t in list) {
            if (t.javaClass == clazz)
                return t as T
        }
        error(clazz.name + " is not a member of list")
    }
}