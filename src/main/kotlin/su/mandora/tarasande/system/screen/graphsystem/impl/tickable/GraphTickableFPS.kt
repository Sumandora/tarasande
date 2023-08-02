package su.mandora.tarasande.system.screen.graphsystem.impl.tickable

import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventPollEvents
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.screen.graphsystem.GraphTickable

class GraphTickableFPS : GraphTickable("Game", "FPS", 200, true) {

    private val fpsBooster = ValueBoolean(this, "FPS 1337-ify", false)
    private val multiplier = ValueNumber(this, "Multiplier", 1.0, 1.0, 10.0, 0.1, isEnabled = { fpsBooster.value })
    private val base = ValueNumber(this, "Base", 0.0, 0.0, 1000.0, 50.0, isEnabled = { fpsBooster.value })

    private val data = ArrayList<Long>()

    init {
        EventDispatcher.add(EventPollEvents::class.java) {
            data.removeIf { time -> System.currentTimeMillis() - time > 1000L }
            data.add(System.currentTimeMillis())
        }
    }

    override fun tick(): Number {
        return data.size.toDouble().let {
            if (fpsBooster.value) base.value + it * multiplier.value
            else it
        }
    }
}