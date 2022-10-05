package net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.elements.impl.notification

import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.valuecomponent.ValueComponent
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.elements.PanelElements

class PanelElementsNotification(x: Double, y: Double, val screenCheatMenu: ScreenCheatMenu) : PanelElements<ValueComponent>("Notifications", x, y, 150.0, 100.0, fixed = true) {
    override fun isVisible() = elementList.isNotEmpty()
}