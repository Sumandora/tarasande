package net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl.tickable

import net.tarasandedevelopment.tarasande.event.EventPollEvents
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.GraphTickable
import su.mandora.event.EventDispatcher

class GraphTickableFPS : GraphTickable("Game", "FPS", 200, true) {

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