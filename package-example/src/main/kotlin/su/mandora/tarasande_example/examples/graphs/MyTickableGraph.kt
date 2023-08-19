package su.mandora.tarasande_example.examples.graphs

import su.mandora.tarasande.system.screen.graphsystem.GraphTickable
import java.util.concurrent.ThreadLocalRandom

class MyTickableGraph : GraphTickable("My graphs", "My tickable graph", 100, true) {
    override fun tick() = ThreadLocalRandom.current().nextInt()
}