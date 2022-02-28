package su.mandora.tarasande.base.event

import su.mandora.tarasande.base.Manager
import java.lang.reflect.ParameterizedType
import java.util.function.Consumer

class ManagerEvent : Manager<Pair<Int, Consumer<Event>>>() {

    fun call(event: Event) {
        list.forEach { it.second.accept(event) }
    }

    fun addObject(any: Any) {
        for (field in any.javaClass.declaredFields) {
            if (field.type === Consumer::class.java) {
                if (((field.genericType as ParameterizedType).actualTypeArguments[0]) === Event::class.java) {
                    field.isAccessible = true
                    val consumer = field.get(any)
                    if (!list.contains(consumer)) {
                        var priority = 1000
                        if (field.isAnnotationPresent(Priority::class.java)) {
                            priority = field.getAnnotation(Priority::class.java).value
                        }
                        add(Pair(priority, consumer as Consumer<Event>))
                    }
                }
            }
        }
    }

    fun remObject(any: Any) {
        for (field in any.javaClass.declaredFields) {
            if (field.type === Consumer::class.java) {
                if (((field.genericType as ParameterizedType).actualTypeArguments[0]) === Event::class.java) {
                    field.isAccessible = true
                    for (pair in list)
                        if (pair.second == field.get(any))
                            rem(pair)
                }
            }
        }
    }

    fun add(eventConsumer: Consumer<Event>) {
        add(Pair(1000, eventConsumer))
    }

    override fun add(vararg objects: Pair<Int, Consumer<Event>>) {
        super.add(*objects)
        list.sortWith(Comparator.comparingInt { it.first })
    }
}

open class Event(private val cancellable: Boolean) {
    var cancelled = false

    fun setCancelled() {
        if (cancellable) {
            cancelled = true
        } else {
            throw IllegalStateException("Event is not cancellable")
        }
    }
}

annotation class Priority(val value: Int)