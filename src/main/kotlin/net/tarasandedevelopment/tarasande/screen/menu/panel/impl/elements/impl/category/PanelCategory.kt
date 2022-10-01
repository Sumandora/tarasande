package net.tarasandedevelopment.tarasande.screen.menu.panel.impl.elements.impl.category

import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.screen.menu.panel.impl.elements.PanelElements
import net.tarasandedevelopment.tarasande.util.string.StringUtil

class PanelCategory(private val moduleCategory: ModuleCategory, x: Double, y: Double) : PanelElements<ElementModule>(StringUtil.formatEnumTypes(moduleCategory.name), x, y, 150.0, 100.0) {
    init {
        TarasandeMain.get().managerModule.list.forEach {
            if (it.category == moduleCategory && it.visibleInMenu)
                elementList.add(ElementModule(it, 100.0))
        }
    }
}