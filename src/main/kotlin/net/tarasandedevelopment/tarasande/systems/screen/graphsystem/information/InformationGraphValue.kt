package net.tarasandedevelopment.tarasande.systems.screen.graphsystem.information

import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.Graph
import net.tarasandedevelopment.tarasande.systems.screen.informationsystem.Information

class InformationGraphValue(private val graph: Graph) : Information("Graph", graph.name) {

    override fun getMessage(): String? {
        return graph.formatHud()
    }
}