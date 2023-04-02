package su.mandora.tarasande.system.screen.graphsystem.impl.tickable

import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventMouse
import su.mandora.tarasande.event.impl.EventSwing
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.screen.graphsystem.GraphTickable

class GraphTickableCPS : GraphTickable("Player", "CPS", 200, true) {

    private var clickMode = ValueMode(this, "Click mode", false, "Hand swing", "Mouse click")
    private val clicks = ArrayList<Long>()

    init {
        EventDispatcher.apply {
            add(EventSwing::class.java) {
                if (clickMode.isSelected(0))
                    clicks.add(System.currentTimeMillis())
            }
            add(EventMouse::class.java) {
                if (clickMode.isSelected(1) && it.action == GLFW.GLFW_PRESS)
                    clicks.add(System.currentTimeMillis())
            }
        }
    }

    override fun tick(): Number {
        clicks.removeIf { click -> System.currentTimeMillis() - click > 1000 }
        return clicks.size
    }
}