package su.mandora.tarasande.screen.menu.panel.impl.elements.impl.category

import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.screen.menu.panel.impl.elements.PanelElements
import su.mandora.tarasande.util.string.StringUtil

class PanelCategory(private val moduleCategory: ModuleCategory, x: Double, y: Double) : PanelElements<ElementModule>(StringUtil.formatEnumTypes(moduleCategory.name), x, y, 150.0, 100.0) {
    init {
        TarasandeMain.get().managerModule.list.forEach {
            if (it.category == moduleCategory && it.visibleInMenu)
                elementList.add(ElementModule(it, 100.0))
        }
    }
}