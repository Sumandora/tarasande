package net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl.tickable

import net.tarasandedevelopment.tarasande.event.EventMouse
import net.tarasandedevelopment.tarasande.event.EventSwing
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.GraphTickable
import org.lwjgl.glfw.GLFW
import su.mandora.event.EventDispatcher

class GraphTickableCPS : GraphTickable("CPS", 200, true) {

    private var clickMode = ValueMode(this, "Click mode", false, "Hand swing", "Mouse click")
    private val clicks = ArrayList<Long>()

    init {
        EventDispatcher.apply {
            add(EventSwing::class.java) {
                if (clickMode.isSelected(0))
                    clicks.add(System.currentTimeMillis())
            }
            add(EventMouse::class.java) {
                if (it.action == GLFW.GLFW_PRESS && clickMode.isSelected(1))
                    clicks.add(System.currentTimeMillis())
            }
        }
    }

    override fun tick(): Number {
        clicks.removeIf { click -> System.currentTimeMillis() - click > 1000 }
        return clicks.size
    }
}