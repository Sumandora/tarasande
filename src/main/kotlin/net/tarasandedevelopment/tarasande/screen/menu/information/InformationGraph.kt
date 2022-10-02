package net.tarasandedevelopment.tarasande.screen.menu.information

import net.tarasandedevelopment.tarasande.base.screen.menu.graph.Graph
import net.tarasandedevelopment.tarasande.base.screen.menu.information.Information

class InformationGraphValue(private val graph: Graph) : Information("Graph", graph.name) {

    override fun getMessage(): String? {
        return graph.formatHud()
    }
}