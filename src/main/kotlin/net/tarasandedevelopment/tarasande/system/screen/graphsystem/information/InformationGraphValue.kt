package net.tarasandedevelopment.tarasande.system.screen.graphsystem.information

import net.tarasandedevelopment.tarasande.system.screen.graphsystem.Graph
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information

class InformationGraphValue(private val graph: Graph) : Information("Graph", graph.name) {

    override fun getMessage(): String? {
        return graph.format(graph.values().lastOrNull() ?: return null)
    }
}