package net.tarasandedevelopment.tarasande.systems.screen.graphsystem.impl.tickable

import net.tarasandedevelopment.tarasande.event.EventPollEvents
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.GraphTickable
import su.mandora.event.EventDispatcher

class GraphFPS : GraphTickable("FPS", 200, true) {

    private val data = ArrayList<Long>()

    init {
        EventDispatcher.add(EventPollEvents::class.java) {
            data.removeIf { System.currentTimeMillis() - it > 1000L }
            data.add(System.currentTimeMillis())
        }
    }

    override fun tick(): Number {
        return data.size.toDouble()
    }
}