package net.tarasandedevelopment.tarasande.panelsystem.impl.elements.impl.clientvalues

import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.valuecomponent.ElementValueComponent
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.elements.PanelElements

class PanelElementsClientValues(screenCheatMenu: ScreenCheatMenu, x: Double, y: Double) : PanelElements<ElementValueComponent>("Client values", x, y, 150.0, 100.0) {
    init {
        for (it in TarasandeMain.get().valueSystem.getValues(TarasandeMain.get().clientValues)) {
            elementList.add(screenCheatMenu.managerValueComponent.newInstance(it)!!)
        }
    }
}