package net.tarasandedevelopment.tarasande.clientmenu

import net.tarasandedevelopment.tarasande.base.clientmenu.ElementMenuScreen
import net.tarasandedevelopment.tarasande.base.clientmenu.ManagerClientMenu
import net.tarasandedevelopment.tarasande.value.ValueMode

class MenuSettingsParent(managerMenu: ManagerClientMenu) {

    val focusedMenuEntry: ValueMode

    init {
        val entries = mutableListOf("None")
        entries.addAll(managerMenu.list.filterIsInstance<ElementMenuScreen>().map { e -> e.name })

        focusedMenuEntry = ValueMode(this, "Focused menu entry", false, *entries.toTypedArray())
    }
}
