package su.mandora.tarasande.system.screen.graphsystem

import su.mandora.tarasande.Manager
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventSuccessfulLoad
import su.mandora.tarasande.event.impl.EventTick
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.screen.graphsystem.impl.GraphPing
import su.mandora.tarasande.system.screen.graphsystem.impl.GraphTPS
import su.mandora.tarasande.system.screen.graphsystem.impl.rotation.GraphPitchDelta
import su.mandora.tarasande.system.screen.graphsystem.impl.rotation.GraphYawDelta
import su.mandora.tarasande.system.screen.graphsystem.impl.tickable.*
import su.mandora.tarasande.system.screen.graphsystem.impl.tickable.connection.packet.GraphTickableRX
import su.mandora.tarasande.system.screen.graphsystem.impl.tickable.connection.packet.GraphTickableTX
import su.mandora.tarasande.system.screen.graphsystem.impl.tickable.connection.traffic.GraphTickableIncomingTraffic
import su.mandora.tarasande.system.screen.graphsystem.impl.tickable.connection.traffic.GraphTickableOutgoingTraffic
import su.mandora.tarasande.system.screen.graphsystem.information.InformationGraphValue
import su.mandora.tarasande.system.screen.graphsystem.panel.PanelGraph
import su.mandora.tarasande.system.screen.informationsystem.ManagerInformation
import su.mandora.tarasande.system.screen.panelsystem.ManagerPanel
import su.mandora.tarasande.util.extension.kotlinruntime.roundTo
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.pow
import kotlin.math.roundToInt

object ManagerGraph : Manager<Graph>() {

    init {
        add(
            // Normal ones
            GraphTickableFPS(),
            GraphTPS(),
            GraphTickableCPS(),
            GraphYawDelta(),
            GraphPitchDelta(),
            GraphPing(),

            // Tickable ones
            GraphTickableMotion(),
            GraphTickableOnlinePlayers(),
            GraphTickableMemory(),
            GraphTickableIncomingTraffic(),
            GraphTickableOutgoingTraffic(),
            GraphTickableTX(),
            GraphTickableRX()
        )

        EventDispatcher.apply {
            add(EventSuccessfulLoad::class.java) {
                ManagerInformation.add(*list.map { e -> InformationGraphValue(e) }.toTypedArray())
                ManagerPanel.add(*list.map { e -> PanelGraph(e) }.toTypedArray())
            }
        }
    }
}

@Suppress("LeakingThis")
open class Graph(val category: String, val name: String, bufferLength: Int, integer: Boolean) {
    var decimalPlaces = 0
    private var bufferLength = ValueNumber(this, "Buffer length", 1.0, bufferLength / 2.0, bufferLength.toDouble(), 1.0)

    init {
        if (!integer)
            object : ValueNumber(this, "Decimal places", 0.0, 1.0, 5.0, 1.0) {
                override fun onChange(oldValue: Double?, newValue: Double) {
                    decimalPlaces = newValue.toInt()
                }
            }


    }

    private val values = CopyOnWriteArrayList<Number>()

    fun add(num: Number) {
        values.add(num)
        while (values.size > bufferLength.value)
            values.removeAt(0)
    }

    fun isEmpty() = values.isEmpty()

    fun values() = values.toTypedArray()

    fun clear() = values.clear()

    open fun format(num: Number?): String? {
        if(num == null)
            return null
        return if (decimalPlaces > 0.0)
            num.toDouble().roundTo(10.0.pow(decimalPlaces)).toString()
        else
            num.toDouble().roundToInt().toString() // Cast to int for no comma
    }
}

abstract class GraphTickable(category: String, name: String, bufferLength: Int, integer: Boolean) : Graph(category, name, bufferLength, integer) {

    init {
        EventDispatcher.add(EventTick::class.java) {
            if (it.state == EventTick.State.PRE)
                add(tick() ?: return@add)
        }
    }

    abstract fun tick(): Number?
}