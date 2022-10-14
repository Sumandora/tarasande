package net.tarasandedevelopment.eventsystem

import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventTick
import java.util.function.Consumer

class EventDispatcher {

    private val eventListeners = HashMap<Class<*>, ArrayList<Pair<Consumer<Event>, Int>>>()

    fun call(event: Event) {
        if(TarasandeMain.get().disabled)
            return
        eventListeners[event::class.java]?.forEach { it.first.accept(event) }
    }

    fun <T : Event> add(clazz: Class<T>, priority: Int = 1000, c: Consumer<T>) {
        eventListeners.computeIfAbsent(clazz){ArrayList()}.also {
            @Suppress("UNCHECKED_CAST") // bypass generics $$$$$
            it.add(Pair(c as Consumer<Event>, priority))
            it.sortBy { it.second }
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

@Target(AnnotationTarget.FIELD)
@Retention
annotation class Priority(val value: Int)

fun main() {
    val eventDispatcher = EventDispatcher()
    eventDispatcher.add(EventTick::class.java) {
        println(it.state)
    }

    eventDispatcher.call(EventTick(EventTick.State.PRE))
    eventDispatcher.call(EventTick(EventTick.State.POST))
}