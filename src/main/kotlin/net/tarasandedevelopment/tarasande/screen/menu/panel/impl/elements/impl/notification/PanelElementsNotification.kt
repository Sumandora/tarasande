package net.tarasandedevelopment.tarasande.screen.menu.panel.impl.elements.impl.notification

import net.tarasandedevelopment.tarasande.base.screen.menu.valuecomponent.ValueComponent
import net.tarasandedevelopment.tarasande.screen.menu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.menu.panel.impl.elements.PanelElements
import net.tarasandedevelopment.tarasande.util.chat.CommunicationUtil

class PanelElementsNotification(x: Double, y: Double, val screenCheatMenu: ScreenCheatMenu) : PanelElements<ValueComponent>("Notifications", x, y, 150.0, 100.0) {

    init {
        CommunicationUtil.add("Here you can see all notifications")
    }
}
