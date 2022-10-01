package net.tarasandedevelopment.tarasande.screen.menu.panel.impl.elements.impl.clientvalues

import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.menu.valuecomponent.ValueComponent
import net.tarasandedevelopment.tarasande.screen.menu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.menu.panel.impl.elements.PanelElements

class PanelClientValues(screenCheatMenu: ScreenCheatMenu, x: Double, y: Double) : PanelElements<ValueComponent>("Client values", x, y, 150.0, 100.0) {
    init {
        for (it in TarasandeMain.get().managerValue.getValues(TarasandeMain.get().clientValues)) {
            elementList.add(screenCheatMenu.managerValueComponent.newInstance(it)!!)
        }
    }
}