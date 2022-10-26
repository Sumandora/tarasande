package net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.elements.impl.creative

import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.valuecomponent.ElementValueComponent
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.elements.PanelElements

class PanelElementsCreative(x: Double, y: Double, val screenCheatMenu: ScreenCheatMenu) : PanelElements<ElementValueComponent>("Creative Exploits", x, y, 150.0, 100.0) {

    init {
        for (it in TarasandeMain.get().managerValue.getValues(screenCheatMenu.managerCreative)) {
            elementList.add(screenCheatMenu.managerValueComponent.newInstance(it)!!)
        }
    }
}