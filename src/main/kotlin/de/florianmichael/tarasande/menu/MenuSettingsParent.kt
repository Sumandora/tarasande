package de.florianmichael.tarasande.menu

import de.florianmichael.tarasande.base.menu.ElementMenuScreen
import de.florianmichael.tarasande.base.menu.ManagerMenu
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.value.ValueMode

class MenuSettingsParent(managerMenu: ManagerMenu) {

    val focusedMenuEntry: ValueMode

    init {
        val entries = mutableListOf("None")
        entries.addAll(managerMenu.list.filterIsInstance<ElementMenuScreen>().map { e -> e.name })

        focusedMenuEntry = ValueMode(this, "Focused menu entry", false, *entries.toTypedArray())
    }
}
