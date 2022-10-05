package net.tarasandedevelopment.tarasande.screen.cheatmenu.information

import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.graph.Graph
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.information.Information

class InformationGraphValue(private val graph: Graph) : Information("Graph", graph.name) {

    override fun getMessage(): String? {
        return graph.formatHud()
    }
}