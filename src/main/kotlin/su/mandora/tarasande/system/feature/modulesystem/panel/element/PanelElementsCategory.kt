package su.mandora.tarasande.system.feature.modulesystem.panel.element

import su.mandora.tarasande.system.feature.modulesystem.ManagerModule
import su.mandora.tarasande.system.screen.panelsystem.api.PanelElements

class PanelElementsCategory(moduleSystem: ManagerModule, private val moduleCategory: String) : PanelElements<ElementWidthModule>(moduleCategory, 150.0, 100.0) {
    init {
        moduleSystem.list.forEach {
            if (it.category == moduleCategory)
                elementList.add(ElementWidthModule(it, 100.0))
        }
    }
}