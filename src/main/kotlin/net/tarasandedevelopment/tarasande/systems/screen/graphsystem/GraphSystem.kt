package net.tarasandedevelopment.tarasande.systems.screen.graphsystem

import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.impl.GraphOnlinePlayers
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.impl.GraphPing
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.impl.GraphTPS
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.impl.tickable.GraphCPS
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.impl.tickable.GraphFPS
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.impl.tickable.GraphMemory
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.impl.tickable.GraphMotion
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.impl.tickable.connection.packets.GraphRX
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.impl.tickable.connection.packets.GraphTX
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.impl.tickable.connection.traffic.GraphIncomingTraffic
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.impl.tickable.connection.traffic.GraphOutgoingTraffic
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.impl.tickable.rotation.GraphPitchDelta
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.impl.tickable.rotation.GraphYawDelta
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.information.InformationGraphValue
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.panel.PanelGraph
import net.tarasandedevelopment.tarasande.systems.screen.informationsystem.ManagerInformation
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.ManagerPanel
import su.mandora.event.EventDispatcher
import kotlin.math.pow
import kotlin.math.roundToInt

class ManagerGraph(informationSystem: ManagerInformation, panelSystem: ManagerPanel) : Manager<Graph>() {

    init {
        add(
            GraphFPS(),
            GraphTPS(),
            GraphCPS(),
            GraphYawDelta(),
            GraphPitchDelta(),
            GraphMotion(),
            GraphPing(),
            GraphOnlinePlayers(),
            GraphMemory(),
            GraphIncomingTraffic(),
            GraphOutgoingTraffic(),
            GraphTX(),
            GraphRX()
        )

        EventDispatcher.apply {
            add(EventSuccessfulLoad::class.java) {
                informationSystem.add(*list.map { e -> InformationGraphValue(e) }.toTypedArray())
                panelSystem.add(*list.map { e -> PanelGraph(e) }.toTypedArray())
            }

            add(EventTick::class.java) {
                if (it.state == EventTick.State.PRE)
                    for (graph in list)
                        if(graph is GraphTickable)
                            graph.add(graph.tick() ?: continue)
            }
        }
    }
}

open class Graph(val name: String, val bufferLength: Int, val integer: Boolean) {
    var decimalPlaces = 0

    init {
        if(!integer)
            object : ValueNumber(this, "Decimal places", 0.0, 1.0, 5.0, 1.0) {
                override fun onChange() {
                    decimalPlaces = value.toInt()
                }
            }
    }

    private val values = ArrayList<Number>()

    fun add(num: Number) {
        values.add(num)
        while(values.size > bufferLength)
            values.removeAt(0)
    }

    fun isEmpty() = values.isEmpty()

    fun values() = values.toTypedArray()

    fun clear() = values.clear()

    open fun format(num: Number?) = if(!integer) {
            val rounding = 10.0.pow(decimalPlaces.toDouble())
            num?.toDouble()?.times(rounding)?.roundToInt()?.div(rounding)?.toString()
        } else num?.toInt()?.toString()
}

abstract class GraphTickable(name: String, bufferLength: Int, integer: Boolean) : Graph(name, bufferLength, integer) {

    init {
        EventDispatcher.add(EventTick::class.java) {
            if(it.state == EventTick.State.PRE)
                add(tick() ?: return@add)
        }
    }

    abstract fun tick(): Number?
}