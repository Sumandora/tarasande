package su.mandora.tarasande.system.screen.graphsystem.impl.tickable

import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventPollEvents
import su.mandora.tarasande.system.screen.graphsystem.GraphTickable

class GraphTickableFPS : GraphTickable("Game", "FPS", 200, true) {

    private val data = ArrayList<Long>()

    init {
        EventDispatcher.add(EventPollEvents::class.java) {
            data.removeIf { time -> System.currentTimeMillis() - time > 1000L }
            data.add(System.currentTimeMillis())
        }
    }

    override fun tick(): Number {
        return data.size.toDouble()
    }
}