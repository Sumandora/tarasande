package su.mandora.event

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

object EventDispatcher {

    private val eventListeners = ConcurrentHashMap<Class<*>, CopyOnWriteArrayList<Pair<Consumer<Event>, Int>>>()

    fun call(event: Event) {
        eventListeners[event::class.java]?.forEach { it.first.accept(event) }
    }

    fun <T : Event> add(clazz: Class<T>, priority: Int = 1000, c: Consumer<T>) {
        eventListeners.computeIfAbsent(clazz) { CopyOnWriteArrayList() }.also {
            @Suppress("UNCHECKED_CAST") // bypass generics $$$$$
            it.add(Pair(c as Consumer<Event>, priority))
            it.sortBy { listener -> listener.second }
        }
    }

    fun <T : Event> rem(clazz: Class<T>, c: Consumer<T>) {
        eventListeners[clazz]?.removeAll { it.first == c }
    }
}

open class Event(private val cancellable: Boolean) {
    var cancelled = false
        set(value) = if (cancellable || !value) {
            field = value
        } else {
            error("Event is not cancellable")
        }
}
