package net.tarasandedevelopment.tarasande.screen.menu.information

import net.tarasandedevelopment.tarasande.base.screen.menu.graph.Graph
import net.tarasandedevelopment.tarasande.base.screen.menu.information.Information
import net.tarasandedevelopment.tarasande.screen.menu.panel.impl.fixed.impl.PanelFixedInformation

class InformationGraphValue(private val graph: Graph) : Information("Graph", graph.name) {

    override fun getMessage(parent: PanelFixedInformation): String? {
        return graph.formatHud()
    }
}