package net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.elements.impl.clientvalues

import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.valuecomponent.ValueComponent
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.elements.PanelElements

class PanelElementsClientValues(screenCheatMenu: ScreenCheatMenu, x: Double, y: Double) : PanelElements<ValueComponent>("Client values", x, y, 150.0, 100.0) {
    init {
        for (it in TarasandeMain.get().managerValue.getValues(TarasandeMain.get().clientValues)) {
            elementList.add(screenCheatMenu.managerValueComponent.newInstance(it)!!)
        }
    }
}