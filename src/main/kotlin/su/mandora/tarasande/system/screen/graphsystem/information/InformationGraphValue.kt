package su.mandora.tarasande.system.screen.graphsystem.information

import su.mandora.tarasande.system.screen.graphsystem.Graph
import su.mandora.tarasande.system.screen.informationsystem.Information

class InformationGraphValue(private val graph: Graph) : Information(graph.category, graph.name) {

    override fun getMessage(): String? {
        return graph.format(graph.values().lastOrNull() ?: return null)
    }
}