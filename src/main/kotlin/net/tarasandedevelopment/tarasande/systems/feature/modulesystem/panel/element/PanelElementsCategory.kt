package net.tarasandedevelopment.tarasande.systems.feature.modulesystem.panel.element

import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ManagerModule
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.api.PanelElements

class PanelElementsCategory(moduleSystem: ManagerModule, private val moduleCategory: String) : PanelElements<ElementModule>(moduleCategory, 150.0, 100.0) {
    init {
        moduleSystem.list.forEach {
            if (it.category == moduleCategory)
                elementList.add(ElementModule(it, 100.0))
        }
    }
}