package net.tarasandedevelopment.tarasande.panelsystem.impl.elements.impl.category

import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.elements.PanelElements

class PanelElementsCategory(private val moduleCategory: String, x: Double, y: Double) : PanelElements<ElementModule>(moduleCategory, x, y, 150.0, 100.0) {
    init {
        TarasandeMain.get().managerModule.list.forEach {
            if (it.category == moduleCategory)
                elementList.add(ElementModule(it, 100.0))
        }
    }
}