package net.tarasandedevelopment.tarasande.system.screen.graphsystem

import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl.GraphPing
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl.GraphTPS
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl.tickable.*
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl.tickable.connection.packet.GraphTickableRX
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl.tickable.connection.packet.GraphTickableTX
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl.tickable.connection.traffic.GraphTickableIncomingTraffic
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl.tickable.connection.traffic.GraphTickableOutgoingTraffic
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl.tickable.rotation.GraphTickablePitchDelta
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl.tickable.rotation.GraphTickableYawDelta
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.information.InformationGraphValue
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.panel.PanelGraph
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.ManagerInformation
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.ManagerPanel
import su.mandora.event.EventDispatcher
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.pow
import kotlin.math.roundToInt

object ManagerGraph : Manager<Graph>() {

    init {
        add(
            GraphTickableFPS(),
            GraphTPS(),
            GraphTickableCPS(),
            GraphTickableYawDelta(),
            GraphTickablePitchDelta(),
            GraphTickableMotion(),
            GraphPing(),
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
    var bufferLength = 0

    init {
        if (!integer)
            object : ValueNumber(this, "Decimal places", 0.0, 1.0, 5.0, 1.0) {
                override fun onChange(oldValue: Double?, newValue: Double) {
                    decimalPlaces = newValue.toInt()
                }
            }

        object : ValueNumber(this, "Buffer length", 1.0, bufferLength / 2.0, bufferLength.toDouble(), 1.0) {
            override fun onChange(oldValue: Double?, newValue: Double) {
                this@Graph.bufferLength = newValue.toInt()
            }
        }
    }

    private val values = CopyOnWriteArrayList<Number>()

    fun add(num: Number) {
        values.add(num)
        while (values.size > bufferLength)
            values.removeAt(0)
    }

    fun isEmpty() = values.isEmpty()

    fun values() = values.toTypedArray()

    fun clear() = values.clear()

    open fun format(num: Number?) = if (decimalPlaces > 0.0) {
        val rounding = 10.0.pow(decimalPlaces.toDouble())
        num?.toDouble()?.times(rounding)?.roundToInt()?.div(rounding)?.toString()
    } else num?.toDouble()?.roundToInt()?.toString()
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