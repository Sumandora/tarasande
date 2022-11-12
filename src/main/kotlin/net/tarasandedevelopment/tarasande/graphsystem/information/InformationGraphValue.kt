package net.tarasandedevelopment.tarasande.graphsystem.information

import net.tarasandedevelopment.tarasande.graphsystem.Graph
import net.tarasandedevelopment.tarasande.informationsystem.Information

class InformationGraphValue(private val graph: Graph) : Information("Graph", graph.name) {

    override fun getMessage(): String? {
        return graph.formatHud()
    }
}